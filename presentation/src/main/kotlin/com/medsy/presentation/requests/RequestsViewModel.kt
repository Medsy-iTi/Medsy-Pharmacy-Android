package com.medsy.presentation.requests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.domain.orders.model.PharmacyRequestDomain
import com.medsy.domain.orders.model.RequestStatusConstants
import com.medsy.domain.orders.usecase.GetCurrentPharmacyRequestsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
import javax.inject.Inject

@HiltViewModel
class RequestsViewModel @Inject constructor(
    private val getCurrentPharmacyRequestsUseCase: GetCurrentPharmacyRequestsUseCase,
    private val submittedOffersManager: SubmittedOffersManager,
) : ViewModel() {
    private val mutableState = MutableStateFlow(RequestsUIState())
    val state = mutableState
        .onStart { loadRequests(refresh = true) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), RequestsUIState())

    private val mutableEffect = Channel<RequestsUIEffect>(Channel.BUFFERED)
    val effect = mutableEffect.receiveAsFlow()

    private var nextPage = 0
    private var reachedLastPage = false

    init {
        viewModelScope.launch {
            submittedOffersManager.submittedRequestIds.collect { answeredIds ->
                mutableState.update { current ->
                    current.copy(requests = current.requests.filterNot { it.id in answeredIds })
                }
            }
        }
    }

    fun onIntent(intent: RequestsUIIntent) {
        when (intent) {
            is RequestsUIIntent.SearchQueryChanged -> mutableState.update { it.copy(searchQuery = intent.query) }
            is RequestsUIIntent.RequestClicked -> sendEffect(
                RequestsUIEffect.NavigateToRequestDetails(
                    intent.requestId
                )
            )

            RequestsUIIntent.Refresh,
            RequestsUIIntent.Retry -> loadRequests(refresh = true)

            RequestsUIIntent.LoadMore -> loadRequests(refresh = false)
        }
    }

    private fun loadRequests(refresh: Boolean) {
        if (!refresh && (mutableState.value.isLoadingMore || reachedLastPage)) return
        viewModelScope.launch {
            val page = if (refresh) 0 else nextPage
            if (refresh) {
                mutableState.update { it.copy(isLoading = true, hasError = false) }
            } else {
                mutableState.update { it.copy(isLoadingMore = true) }
            }

            getCurrentPharmacyRequestsUseCase(page, PAGE_SIZE, null)
                .onSuccess { response ->
                    val answeredIds = submittedOffersManager.submittedRequestIds.value
                    val incoming = response.content
                        .filter {
                            it.status.equals(
                                RequestStatusConstants.SEARCHING,
                                ignoreCase = true
                            )
                        }
                        .filterNot { it.id in answeredIds }
                        .map(PharmacyRequestDomain::toWorkItem)
                    reachedLastPage = response.last
                    nextPage = response.pageNumber + 1
                    mutableState.update { current ->
                        val combined =
                            if (refresh) incoming else (current.requests + incoming).distinctBy { it.id }
                        current.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            hasError = false,
                            canLoadMore = !reachedLastPage,
                            requests = combined,
                        )
                    }
                }
                .onError {
                    mutableState.update { current ->
                        current.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            hasError = current.requests.isEmpty(),
                        )
                    }
                }
        }
    }

    private fun sendEffect(effect: RequestsUIEffect) {
        viewModelScope.launch { mutableEffect.send(effect) }
    }

    private companion object {
        const val PAGE_SIZE = 20
    }
}

internal fun calculateMinutesAgoOrNull(createdAt: String?): Int? {
    if (createdAt.isNullOrBlank() || !createdAt.contains('T')) return null
    return runCatching {
        val normalized = if (createdAt.endsWith("Z")) createdAt else "${createdAt}Z"
        Duration.between(Instant.parse(normalized), Instant.now()).toMinutes().coerceAtLeast(0)
            .toInt()
    }.getOrNull()
}

private fun PharmacyRequestDomain.toWorkItem() = PharmacyWorkItem(
    id = id,
    displayId = id.toString(),
    source = PharmacyWorkSource.Request,
    status = PharmacyWorkStatus.Searching,
    createdAt = createdAt,
    minutesAgo = calculateMinutesAgoOrNull(createdAt),
    customerName = customerName,
    customerId = customerId,
    customerPhone = customerPhone,
    customerAddress = deliveryAddress,
    productImages = items.map { it.imageUrl },
    total = items.sumOf { it.unitPrice * it.quantity },
    paymentMethod = PaymentMethod.fromApiValue(paymentMethod),
)
