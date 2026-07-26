package com.medsy.presentation.orderdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.domain.orders.usecase.GetRequestDetailsUseCase
import com.medsy.presentation.R
import com.medsy.presentation.orderdetails.mapper.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import com.medsy.domain.orders.usecase.CreateOfferUseCase
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class RequestDetailsViewModel @Inject constructor(
    private val getRequestDetailsUseCase: GetRequestDetailsUseCase,
    private val createOfferUseCase: CreateOfferUseCase,
    private val submittedOffersManager: com.medsy.presentation.orders.SubmittedOffersManager,
    private val substituteResultManager: SubstituteResultManager
) : ViewModel() {

    private var loadedRequestId: Long? = null

    private val _state = MutableStateFlow(RequestDetailsUIState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = RequestDetailsUIState(),
        )

    private val mutableEffect = Channel<RequestDetailsUIEffect>(Channel.BUFFERED)
    val effect = mutableEffect.receiveAsFlow()

    init {
        viewModelScope.launch {
            substituteResultManager.results.collect { result ->
                onIntent(
                    RequestDetailsUIIntent.SubstituteSelected(
                        itemId = result.requestItemId,
                        productId = result.productId,
                        productName = result.productName,
                        productPrice = result.productPrice,
                        productImage = result.productImage
                    )
                )
            }
        }
    }

    fun onIntent(intent: RequestDetailsUIIntent) {
        when (intent) {
            is RequestDetailsUIIntent.LoadRequest -> {
                val idLong = intent.requestId
                if (loadedRequestId != idLong) {
                    loadedRequestId = idLong
                    loadRequest(idLong)
                }
            }

            RequestDetailsUIIntent.BackClicked -> sendEffect(RequestDetailsUIEffect.NavigateBack)

            RequestDetailsUIIntent.CallCustomerClicked -> {
                val phone = _state.value.request?.customerPhone ?: return
                sendEffect(RequestDetailsUIEffect.DialPhoneNumber(phone))
            }

            RequestDetailsUIIntent.OpenLocationClicked ->
                sendEffect(RequestDetailsUIEffect.OpenLocationOnMap)

            RequestDetailsUIIntent.ViewPaymentSummaryClicked ->
                sendEffect(RequestDetailsUIEffect.OpenPaymentSummary)

            RequestDetailsUIIntent.RejectRequestClicked -> rejectRequest()

            RequestDetailsUIIntent.ContactCustomerClicked ->
                sendEffect(RequestDetailsUIEffect.OpenCustomerChat)

            RequestDetailsUIIntent.AcceptRequestClicked -> acceptRequest()

            is RequestDetailsUIIntent.PharmacistNotesChanged -> {
                _state.update { it.copy(pharmacistNotes = intent.notes) }
            }

            is RequestDetailsUIIntent.ToggleItemSelection -> {
                _state.update { currentState ->
                    val newSelection = currentState.selectedItems.toMutableSet()
                    if (newSelection.contains(intent.itemId)) {
                        newSelection.remove(intent.itemId)
                    } else {
                        newSelection.add(intent.itemId)
                    }
                    currentState.copy(selectedItems = newSelection)
                }
            }
            is RequestDetailsUIIntent.AddSubstituteClicked -> {
                sendEffect(RequestDetailsUIEffect.NavigateToSubstituteSearch(intent.itemId))
            }
            is RequestDetailsUIIntent.SubstituteSelected -> {
                _state.update { currentState ->
                    val order = currentState.request ?: return@update currentState
                    val updatedItems = order.items.map { item ->
                        if (item.id == intent.itemId.toString()) {
                            item.copy(
                                productId = intent.productId,
                                name = intent.productName,
                                price = intent.productPrice,
                                imageUrl = intent.productImage
                            )
                        } else {
                            item
                        }
                    }
                    val updatedOrder = order.copy(
                        items = updatedItems,
                        total = updatedItems.sumOf { it.price * it.quantity }
                    )
                    val newSelection = currentState.selectedItems.toMutableSet()
                    newSelection.add(intent.itemId)
                    currentState.copy(request = updatedOrder, selectedItems = newSelection)
                }
            }
        }
    }

    private fun loadRequest(id: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            getRequestDetailsUseCase(id)
                .onSuccess { domainRequest ->
                    if (domainRequest != null) {
                        val presentationRequest = domainRequest.toPresentation()
                        val allItemsIds = presentationRequest.items.map { it.id.toLongOrNull() ?: -1L }.toSet()
                        _state.update {
                            it.copy(
                                isLoading = false,
                                request = presentationRequest,
                                selectedItems = allItemsIds
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                isLoading = false,
                            )
                        }
                    }
                }
                .onError {
                    _state.update {
                        it.copy(
                            isLoading = false,
                        )
                    }
                }
        }
    }

    private fun rejectRequest() {
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true) }
            delay(300.milliseconds) // simulated backend call
            _state.update { it.copy(isSubmitting = false) }
            sendEffect(RequestDetailsUIEffect.ShowMessage(R.string.request_details_rejected_message))
            sendEffect(RequestDetailsUIEffect.NavigateBack)
        }
    }

    private fun acceptRequest() {
        val currentState = _state.value
        val order = currentState.request ?: return
        val requestId = order.id.removePrefix("#").toLongOrNull() ?: return
        val selectedIds = currentState.selectedItems
        
        if (selectedIds.isEmpty()) {
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true) }
            
            val itemsToSubmit = order.items
                .filter { (it.id.toLongOrNull() ?: -1L) in selectedIds }
                .map { Pair(it.id.toLongOrNull() ?: -1L, it.productId ?: 0L) }
                
            val result = createOfferUseCase(requestId, itemsToSubmit)
            
            _state.update { it.copy(isSubmitting = false) }
            result.onSuccess {
                submittedOffersManager.addSubmittedRequestId(requestId)
                sendEffect(RequestDetailsUIEffect.ShowMessage(R.string.request_details_accepted_message))
                sendEffect(RequestDetailsUIEffect.NavigateBack)
            }.onError { _ ->
                sendEffect(RequestDetailsUIEffect.ShowMessage(R.string.error_generic))
            }
        }
    }

    private fun sendEffect(effect: RequestDetailsUIEffect) {
        viewModelScope.launch {
            mutableEffect.send(effect)
        }
    }
}
