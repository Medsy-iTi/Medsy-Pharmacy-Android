package com.medsy.presentation.orderdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.domain.offer.model.CreateOfferItem
import com.medsy.domain.offer.model.CreateOfferRequest
import com.medsy.domain.offer.usecase.CreatePharmacyOfferUseCase
import com.medsy.domain.orders.model.PharmacyRequest
import com.medsy.domain.orders.model.PharmacyRequestAssignmentStatus
import com.medsy.domain.orders.model.PharmacyRequestStatus
import com.medsy.domain.orders.usecase.GetRequestDetailsUseCase
import com.medsy.presentation.R
import com.medsy.presentation.orderdetails.substitute.SubstituteResultManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RequestDetailsViewModel @Inject constructor(
    private val getRequestDetailsUseCase: GetRequestDetailsUseCase,
    private val createOfferUseCase: CreatePharmacyOfferUseCase,
    private val substituteResultManager: SubstituteResultManager,
) : ViewModel() {
    private val mutableState = MutableStateFlow(RequestDetailsUIState())
    val state = mutableState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000L),
        RequestDetailsUIState(),
    )
    private val mutableEffect = Channel<RequestDetailsUIEffect>(Channel.BUFFERED)
    val effect = mutableEffect.receiveAsFlow()
    private var requestId: Long? = null
    private var loadJob: Job? = null
    private var offerSubmissionInFlight = false

    init {
        viewModelScope.launch {
            substituteResultManager.results.collect { result ->
                onIntent(
                    RequestDetailsUIIntent.SubstituteSelected(
                        result.requestItemId,
                        result.productId,
                        result.productName,
                        result.productPrice,
                        result.productImage,
                    )
                )
            }
        }
    }

    fun onIntent(intent: RequestDetailsUIIntent) {
        when (intent) {
            is RequestDetailsUIIntent.LoadRequest -> {
                if (requestId != intent.requestId) {
                    requestId = intent.requestId
                    loadRequest(intent.requestId, forceRefresh = false, userRefresh = false)
                }
            }
            RequestDetailsUIIntent.Refresh -> requestId?.let { loadRequest(it, true, true) }
            RequestDetailsUIIntent.Retry -> requestId?.let { loadRequest(it, true, false) }
            RequestDetailsUIIntent.BackClicked -> sendEffect(RequestDetailsUIEffect.NavigateBack)
            RequestDetailsUIIntent.CallCustomerClicked -> mutableState.value.request?.customerPhone
                ?.takeIf(String::isNotBlank)?.let { sendEffect(RequestDetailsUIEffect.DialPhoneNumber(it)) }
            RequestDetailsUIIntent.OpenLocationClicked -> mutableState.value.request?.let { request ->
                val latitude = request.deliveryLatitude ?: return@let
                val longitude = request.deliveryLongitude ?: return@let
                sendEffect(RequestDetailsUIEffect.OpenLocationOnMap(latitude, longitude))
            }
            RequestDetailsUIIntent.SendOfferClicked -> sendOffer()
            is RequestDetailsUIIntent.ToggleItemSelection -> toggleItem(intent.itemId)
            is RequestDetailsUIIntent.AddSubstituteClicked -> if (mutableState.value.canCreateOffer) {
                sendEffect(RequestDetailsUIEffect.NavigateToSubstituteSearch(intent.itemId))
            }
            is RequestDetailsUIIntent.SubstituteSelected -> if (mutableState.value.canCreateOffer) {
                mutableState.update { state ->
                    if (state.request?.items?.none { it.id == intent.itemId } != false) return@update state
                    state.copy(
                        selectedItems = state.selectedItems + intent.itemId,
                        substitutes = state.substitutes + (
                            intent.itemId to SubstituteDraft(
                                intent.productId,
                                intent.productName,
                                intent.productPrice,
                                intent.productImage,
                            )
                        ),
                    )
                }
            }
        }
    }

    private fun toggleItem(itemId: Long) {
        if (!mutableState.value.canCreateOffer) return
        mutableState.update { state ->
            val selection = if (itemId in state.selectedItems) state.selectedItems - itemId else state.selectedItems + itemId
            state.copy(selectedItems = selection)
        }
    }

    private fun loadRequest(id: Long, forceRefresh: Boolean, userRefresh: Boolean) {
        if (userRefresh && mutableState.value.isRefreshing) return
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            if (userRefresh) mutableState.update { it.copy(isRefreshing = true) }
            else if (mutableState.value.request == null) mutableState.update { it.copy(isLoading = true, hasError = false) }

            getRequestDetailsUseCase(id, forceRefresh).onSuccess { request ->
                reconcileRequest(request)
            }.onError {
                mutableState.update { state ->
                    state.copy(
                        isLoading = false,
                        isRefreshing = false,
                        hasError = state.request == null,
                    )
                }
            }
        }
    }

    private fun reconcileRequest(request: PharmacyRequest) {
        mutableState.update { previous ->
            val actionable = request.requestStatus == PharmacyRequestStatus.Searching &&
                request.assignmentStatus == PharmacyRequestAssignmentStatus.Pending
            val validIds = request.items.mapTo(mutableSetOf()) { it.id }
            val initialSelection = request.items.mapTo(mutableSetOf()) { it.id }
            previous.copy(
                isLoading = false,
                isRefreshing = false,
                hasError = false,
                request = request,
                selectedItems = when {
                    !actionable -> emptySet()
                    previous.request == null -> initialSelection
                    else -> previous.selectedItems.intersect(validIds)
                },
                substitutes = if (actionable) previous.substitutes.filterKeys { it in validIds } else emptyMap(),
            )
        }
    }

    private fun sendOffer() {
        val state = mutableState.value
        val request = state.request ?: return
        if (!state.canCreateOffer || offerSubmissionInFlight || state.selectedItems.isEmpty()) return
        offerSubmissionInFlight = true
        viewModelScope.launch {
            try {
                mutableState.update { it.copy(isSubmitting = true) }
                val items = request.items.filter { it.id in state.selectedItems }.map { item ->
                    CreateOfferItem(
                        requestItemId = item.id,
                        productId = state.substitutes[item.id]?.productId ?: item.productId,
                    )
                }
                createOfferUseCase(request.id, CreateOfferRequest(items)).onSuccess {
                    mutableState.update {
                        it.copy(
                            request = request.copy(assignmentStatus = PharmacyRequestAssignmentStatus.OfferCreated),
                            selectedItems = emptySet(),
                            substitutes = emptyMap(),
                        )
                    }
                    sendEffect(RequestDetailsUIEffect.ShowMessage(R.string.request_details_offer_submitted_message))
                }.onError { error ->
                    if (error is MedsyError.Remote.Http && error.statusCode == HTTP_BAD_REQUEST) {
                        loadRequest(request.id, forceRefresh = true, userRefresh = false)
                    } else sendEffect(RequestDetailsUIEffect.ShowMessage(R.string.error_generic))
                }
            } finally {
                offerSubmissionInFlight = false
                mutableState.update { it.copy(isSubmitting = false) }
            }
        }
    }

    private fun sendEffect(effect: RequestDetailsUIEffect) {
        viewModelScope.launch { mutableEffect.send(effect) }
    }

    private companion object { const val HTTP_BAD_REQUEST = 400 }
}
