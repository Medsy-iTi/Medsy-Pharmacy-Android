package com.medsy.presentation.orderdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.domain.offer.model.CreateOfferItem
import com.medsy.domain.offer.model.CreateOfferRequest
import com.medsy.domain.offer.usecase.CreatePharmacyOfferUseCase
import com.medsy.domain.orders.model.PharmacyRequestAssignmentStatus
import com.medsy.domain.orders.usecase.GetRequestDetailsUseCase
import com.medsy.presentation.R
import com.medsy.presentation.orderdetails.mapper.toPresentation
import com.medsy.presentation.orderdetails.substitute.SubstituteResultManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class RequestDetailsViewModel @Inject constructor(
    private val getRequestDetailsUseCase: GetRequestDetailsUseCase,
    private val createOfferUseCase: CreatePharmacyOfferUseCase,
    private val submittedOffersManager: com.medsy.presentation.requests.SubmittedOffersManager,
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

            RequestDetailsUIIntent.OpenLocationClicked -> {
                val lat = _state.value.request?.deliveryLatitude ?: return
                val lng = _state.value.request?.deliveryLongitude ?: return
                sendEffect(RequestDetailsUIEffect.OpenLocationOnMap(lat, lng))
            }

            RequestDetailsUIIntent.ViewPaymentSummaryClicked ->
                sendEffect(RequestDetailsUIEffect.OpenPaymentSummary)

            RequestDetailsUIIntent.AcceptRequestClicked -> acceptRequest()

            is RequestDetailsUIIntent.PharmacistNotesChanged -> {
                if (_state.value.canCreateOffer) {
                    _state.update { it.copy(pharmacistNotes = intent.notes) }
                }
            }

            is RequestDetailsUIIntent.ToggleItemSelection -> {
                if (!_state.value.canCreateOffer) return
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
                if (_state.value.canCreateOffer) {
                    sendEffect(RequestDetailsUIEffect.NavigateToSubstituteSearch(intent.itemId))
                }
            }

            is RequestDetailsUIIntent.SubstituteSelected -> {
                if (!_state.value.canCreateOffer) return
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

            is RequestDetailsUIIntent.OpenPrescriptionImageClicked -> {
                sendEffect(RequestDetailsUIEffect.OpenPrescriptionImage(intent.imageUrl))
            }
        }
    }

    private fun loadRequest(id: Long) {
        viewModelScope.launch {
            if (_state.value.request == null) {
                _state.update { it.copy(isLoading = true) }
            }

            getRequestDetailsUseCase(id)
                .onSuccess { domainRequest ->
                    val presentationRequest = domainRequest.toPresentation()
                    val assignmentStatus = if (
                        id in submittedOffersManager.submittedRequestIds.value
                    ) {
                        PharmacyRequestAssignmentStatus.OfferCreated
                    } else {
                        domainRequest.assignmentStatus
                    }
                    val allItemsIds =
                        presentationRequest.items.map { it.id.toLongOrNull() ?: -1L }.toSet()
                    _state.update {
                        it.copy(
                            isLoading = false,
                            request = presentationRequest,
                            selectedItems = allItemsIds,
                            assignmentStatus = assignmentStatus,
                        )
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

    private fun acceptRequest() {
        val currentState = _state.value
        if (!currentState.canCreateOffer || currentState.isSubmitting) return
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
                .map { CreateOfferItem(it.id.toLongOrNull() ?: -1L, it.productId ?: 0L) }

            val result = createOfferUseCase(requestId, CreateOfferRequest(itemsToSubmit))

            _state.update { it.copy(isSubmitting = false) }
            result.onSuccess {
                submittedOffersManager.addSubmittedRequestId(requestId)
                _state.update {
                    it.copy(assignmentStatus = PharmacyRequestAssignmentStatus.OfferCreated)
                }
                sendEffect(RequestDetailsUIEffect.ShowMessage(R.string.request_details_offer_submitted_message))
                delay(1000.milliseconds)
                sendEffect(RequestDetailsUIEffect.NavigateBack)
            }.onError { error ->
                if (error is MedsyError.Remote.Http && error.statusCode == HTTP_BAD_REQUEST) {
                    refreshAssignmentAfterBadRequest(requestId)
                } else {
                    sendEffect(RequestDetailsUIEffect.ShowMessage(R.string.error_generic))
                }
            }
        }
    }

    private suspend fun refreshAssignmentAfterBadRequest(requestId: Long) {
        getRequestDetailsUseCase(requestId, forceRefresh = true)
            .onSuccess { domainRequest ->
                val assignmentStatus = domainRequest.assignmentStatus
                _state.update { current ->
                    current.copy(
                        request = domainRequest.toPresentation(),
                        assignmentStatus = assignmentStatus,
                    )
                }
                when (assignmentStatus) {
                    PharmacyRequestAssignmentStatus.OfferCreated -> {
                        submittedOffersManager.addSubmittedRequestId(requestId)
                        sendEffect(RequestDetailsUIEffect.ShowMessage(R.string.request_details_offer_already_submitted))
                    }

                    PharmacyRequestAssignmentStatus.Expired ->
                        sendEffect(RequestDetailsUIEffect.ShowMessage(R.string.request_details_request_expired))

                    PharmacyRequestAssignmentStatus.Pending,
                    PharmacyRequestAssignmentStatus.Unknown ->
                        sendEffect(RequestDetailsUIEffect.ShowMessage(R.string.error_generic))
                }
            }
            .onError {
                sendEffect(RequestDetailsUIEffect.ShowMessage(R.string.error_generic))
            }
    }

    private fun sendEffect(effect: RequestDetailsUIEffect) {
        viewModelScope.launch {
            mutableEffect.send(effect)
        }
    }

    private companion object {
        const val HTTP_BAD_REQUEST = 400
    }
}
