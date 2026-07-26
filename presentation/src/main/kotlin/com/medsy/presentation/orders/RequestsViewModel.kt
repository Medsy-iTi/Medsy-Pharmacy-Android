package com.medsy.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.domain.orders.model.PharmacyRequestDomain
import com.medsy.domain.orders.usecase.GetCurrentPharmacyRequestsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
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
    private val getCurrentPharmacyRequestsUseCase: GetCurrentPharmacyRequestsUseCase
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
        }
    }

    private fun loadRequests() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val result = getCurrentPharmacyRequestsUseCase(page = 0, size = 10, sort = listOf("id,desc"))

            result.onSuccess { requestPage ->
                val uiRequests = requestPage.content.map { it.toPresentation() }.sortedByDescending { it.id }
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
    } catch (e: Exception) {
        0
    }
}

fun PharmacyRequestDomain.toPresentation(): RequestSummary {
    val calculatedTotal = items.sumOf { it.unitPrice * it.quantity }
    val minutes = calculateMinutesAgo(createdAt)
    return RequestSummary(
        id = id,
        minutesAgo = minutes,
        status = when (status) {
            "SEARCHING" -> RequestStatus.Searching
            "PENDING", "NEW" -> RequestStatus.New
            "IN_PROGRESS" -> RequestStatus.InProgress
            "DELIVERED" -> RequestStatus.Delivered
            "CANCELLED" -> RequestStatus.Cancelled
            "COMPLETED" -> RequestStatus.Completed
            "EXPIRED" -> RequestStatus.Expired
            else -> RequestStatus.Searching
        },
        customerName = customerName ?: "Customer #$customerId",
        customerPhone = customerPhone ?: "",
        customerAddress = deliveryAddress ?: "",
        productImages = items.map { it.imageUrl },
        total = calculatedTotal,
        paymentMethod = when (paymentMethod?.uppercase()) {
            "VISA" -> PaymentMethod.Visa
            "MASTERCARD" -> PaymentMethod.Mastercard
            else -> PaymentMethod.Cash
        },
        paymentCardLastDigits = null,
    )
}