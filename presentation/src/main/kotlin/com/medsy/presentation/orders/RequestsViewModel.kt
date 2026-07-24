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

            is RequestsUIIntent.RequestClicked ->
                sendEffect(RequestsUIEffect.NavigateToRequestDetails(intent.requestId))

            is RequestsUIIntent.AcceptRequestClicked ->
                sendEffect(RequestsUIEffect.NavigateToRequestDetails(intent.requestId))

            is RequestsUIIntent.PrepareRequestClicked ->
                sendEffect(RequestsUIEffect.NavigateToRequestDetails(intent.requestId))
        }
    }

    private fun loadRequests() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val result = getCurrentPharmacyRequestsUseCase(page = 0, size = 10)

            result.onSuccess { requestPage ->
                val uiRequests = requestPage.content.map { it.toPresentation() }
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

fun PharmacyRequestDomain.toPresentation(): RequestSummary {
    return RequestSummary(
        id = id,
        minutesAgo = createdAt,
        status = when (status) {
            "SEARCHING" -> RequestStatus.Searching
            "PENDING", "NEW" -> RequestStatus.New
            "IN_PROGRESS" -> RequestStatus.InProgress
            "DELIVERED" -> RequestStatus.Delivered
            "CANCELLED" -> RequestStatus.Cancelled
            "COMPLETED" -> RequestStatus.Completed
            else -> RequestStatus.Searching
        },
        customerName = "Customer #$customerId",
        customerPhone = "",
        customerAddress = deliveryAddress ?: "",
        total = 0,
        paymentMethod = PaymentMethod.Cash,
        paymentCardLastDigits = null,
    )
}