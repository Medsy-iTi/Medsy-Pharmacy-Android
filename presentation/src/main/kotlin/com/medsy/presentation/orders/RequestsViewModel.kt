package com.medsy.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.domain.orders.model.PharmacyRequestDomain
import com.medsy.domain.orders.model.RequestStatusConstants
import com.medsy.domain.orders.usecase.GetCurrentPharmacyRequestsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant

@HiltViewModel
class RequestsViewModel @Inject constructor(
    private val getCurrentPharmacyRequestsUseCase: GetCurrentPharmacyRequestsUseCase,
    private val submittedOffersManager: SubmittedOffersManager
) : ViewModel() {
    private val _state = MutableStateFlow(RequestsUIState())
    val state = _state
        .onStart { loadRequests() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = RequestsUIState(),
        )

    private val mutableEffect = Channel<RequestsUIEffect>(Channel.BUFFERED)
    val effect = mutableEffect.receiveAsFlow()

    private var loadJob: Job? = null

    fun onIntent(intent: RequestsUIIntent) {
        when (intent) {
            is RequestsUIIntent.SearchQueryChanged -> _state.update {
                it.copy(searchQuery = intent.query)
            }

            is RequestsUIIntent.FilterSelected -> _state.update {
                it.copy(selectedFilter = intent.filter)
            }

            RequestsUIIntent.FilterIconClicked -> sendEffect(RequestsUIEffect.OpenFilters)

            is RequestsUIIntent.RequestClicked -> {
                val reqId = intent.requestId
                val order = _state.value.orders.find { it.id == reqId }
                if (order?.status in listOf(RequestStatus.Searching, RequestStatus.New, RequestStatus.InProgress)) {
                    sendEffect(RequestsUIEffect.NavigateToRequestDetails(reqId))
                }
            }
            is RequestsUIIntent.AcceptRequestClicked -> {
                val reqId = intent.requestId
                val order = _state.value.orders.find { it.id == reqId }
                if (order?.status in listOf(RequestStatus.Searching, RequestStatus.New)) {
                    sendEffect(RequestsUIEffect.NavigateToRequestDetails(reqId))
                }
            }
            is RequestsUIIntent.PrepareRequestClicked -> {
                val reqId = intent.requestId
                val order = _state.value.orders.find { it.id == reqId }
                if (order?.status == RequestStatus.InProgress) {
                    sendEffect(RequestsUIEffect.NavigateToRequestDetails(reqId))
                }
            }
            is RequestsUIIntent.OfferSubmitted -> {
                // Now handled by SubmittedOffersManager, no op here
            }
            RequestsUIIntent.Refresh -> loadRequests()
        }
    }

    private fun loadRequests() {
        // Cancel any in-flight request to avoid parallel duplicate HTTP calls.
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val result = getCurrentPharmacyRequestsUseCase(page = 0, size = 10, sort = listOf("id,desc"))

            result.onSuccess { requestPage ->
                val submitted = submittedOffersManager.submittedRequestIds.value
                val offerDataMap = submittedOffersManager.submittedOfferData.value
                val uiRequests = requestPage.content
                    .map { request ->
                        var summary = request.toPresentation()
                        if (submitted.contains(request.id) && (summary.status == RequestStatus.New || summary.status == RequestStatus.Searching)) {
                            summary = summary.copy(status = RequestStatus.OfferSubmitted)
                        }
                        offerDataMap[request.id]?.let { offerData ->
                            summary = summary.copy(
                                productImages = offerData.productImages,
                                total = offerData.total,
                            )
                        }
                        summary
                    }
                    .sortedByDescending { it.id }
                _state.update {
                    it.copy(
                        isLoading = false,
                        orders = uiRequests
                    )
                }
            }.onError {
                _state.update {
                    it.copy(
                        isLoading = false,
                    )
                }
            }
        }
    }

    private fun sendEffect(effect: RequestsUIEffect) {
        viewModelScope.launch {
            mutableEffect.send(effect)
        }
    }
}

fun calculateMinutesAgo(createdAt: String): Int {
    return try {
        val parseStr = if (createdAt.endsWith("Z")) createdAt else "${createdAt}Z"
        val created = Instant.parse(parseStr)
        val now = Instant.now()
        Duration.between(created, now).toMinutes().coerceAtLeast(0).toInt()
    } catch (_: Exception) {
        0
    }
}

fun PharmacyRequestDomain.toPresentation(): RequestSummary {
    val calculatedTotal = items.sumOf { it.unitPrice * it.quantity }
    val minutes = calculateMinutesAgo(createdAt)
    return RequestSummary(
        id = id,
        displayId = orderId?.toString() ?: offerId?.toString() ?: id.toString(),
        minutesAgo = minutes,
        status = when (status) {
            RequestStatusConstants.SEARCHING -> RequestStatus.Searching
            RequestStatusConstants.PENDING, RequestStatusConstants.NEW -> RequestStatus.New
            RequestStatusConstants.IN_PROGRESS -> RequestStatus.InProgress
            RequestStatusConstants.DELIVERED -> RequestStatus.Delivered
            RequestStatusConstants.CANCELLED -> RequestStatus.Cancelled
            RequestStatusConstants.COMPLETED -> RequestStatus.Completed
            RequestStatusConstants.EXPIRED -> RequestStatus.Expired
            else -> RequestStatus.Searching
        },
        customerName = customerName,
        customerId = customerId,
        customerPhone = customerPhone ?: "",
        customerAddress = deliveryAddress ?: "",
        productImages = items.map { it.imageUrl },
        total = calculatedTotal,
        paymentMethod = PaymentMethod.fromApiValue(paymentMethod),
        paymentCardLastDigits = null,
    )
}