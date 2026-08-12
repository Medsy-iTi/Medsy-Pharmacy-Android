package com.medsy.presentation.requests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.domain.orders.model.PharmacyRequestAssignmentStatus
import com.medsy.domain.orders.model.PharmacyRequestDomain
import com.medsy.domain.orders.usecase.GetCurrentPharmacyRequestsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
import javax.inject.Inject

@HiltViewModel
class RequestsViewModel @Inject constructor(
    private val getCurrentPharmacyRequestsUseCase: GetCurrentPharmacyRequestsUseCase,
    private val submittedOffersManager: SubmittedOffersManager,
) : ViewModel() {
    private val mutableState = MutableStateFlow(RequestsUIState())
    val state = mutableState
        .onStart { ensureFilterLoaded(mutableState.value.selectedFilter) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), RequestsUIState())

    private val mutableEffect = Channel<RequestsUIEffect>(Channel.BUFFERED)
    val effect = mutableEffect.receiveAsFlow()

    private val loadingJobs = mutableMapOf<RequestsFilter, Job>()

    init {
        viewModelScope.launch {
            submittedOffersManager.submittedRequestIds.collect(::markSubmittedRequests)
        }
    }

    fun onIntent(intent: RequestsUIIntent) {
        when (intent) {
            is RequestsUIIntent.SearchQueryChanged -> mutableState.update {
                it.copy(searchQuery = intent.query)
            }

            is RequestsUIIntent.FilterSelected -> selectFilter(intent.filter)
            is RequestsUIIntent.RequestClicked -> sendEffect(
                RequestsUIEffect.NavigateToRequestDetails(intent.requestId)
            )

            RequestsUIIntent.Refresh,
            RequestsUIIntent.Retry -> loadRequests(mutableState.value.selectedFilter, refresh = true)

            RequestsUIIntent.LoadMore -> loadRequests(mutableState.value.selectedFilter, refresh = false)
        }
    }

    private fun selectFilter(filter: RequestsFilter) {
        mutableState.update { it.copy(selectedFilter = filter) }
        ensureFilterLoaded(filter)
    }

    private fun ensureFilterLoaded(filter: RequestsFilter) {
        val filterState = mutableState.value.filterStates[filter]
        if (filterState?.hasLoaded != true && filterState?.isLoading != true) {
            loadRequests(filter, refresh = true)
        }
    }

    private fun loadRequests(filter: RequestsFilter, refresh: Boolean) {
        val currentFilterState = mutableState.value.filterStates[filter] ?: RequestsFilterState()
        if (!refresh && (currentFilterState.isLoadingMore || !currentFilterState.canLoadMore)) return

        loadingJobs[filter]?.cancel()
        loadingJobs[filter] = viewModelScope.launch {
            val page = if (refresh) 0 else currentFilterState.nextPage
            updateFilterState(filter) { current ->
                if (refresh) {
                    current.copy(isLoading = true, isLoadingMore = false, hasError = false)
                } else {
                    current.copy(isLoadingMore = true, hasError = false)
                }
            }

            getCurrentPharmacyRequestsUseCase(
                page = page,
                size = PAGE_SIZE,
                sort = listOf("id,desc"),
                assignmentStatus = filter.assignmentStatus,
            )
                .onSuccess { response ->
                    val submittedIds = submittedOffersManager.submittedRequestIds.value
                    val optimisticOfferItems = if (filter == RequestsFilter.OfferCreated) {
                        mutableState.value.filterStates.values
                            .flatMap(RequestsFilterState::requests)
                            .filter { it.id in submittedIds }
                            .map { it.copy(status = PharmacyWorkStatus.OfferCreatedRequest) }
                    } else {
                        emptyList()
                    }
                    val incoming = (response.content
                        .map { request -> request.withLocalSubmission(submittedIds).toWorkItem() }
                        .filter { item -> filter.accepts(item.status) } + optimisticOfferItems)
                        .distinctBy(PharmacyWorkItem::id)
                    updateFilterState(filter) { current ->
                        val combined = if (refresh) {
                            incoming
                        } else {
                            (current.requests + incoming).distinctBy(PharmacyWorkItem::id)
                        }
                        current.copy(
                            requests = combined.sortedByNewest(),
                            nextPage = response.pageNumber + 1,
                            canLoadMore = !response.last,
                            hasLoaded = true,
                            isLoading = false,
                            isLoadingMore = false,
                            hasError = false,
                        )
                    }
                }
                .onError {
                    updateFilterState(filter) { current ->
                        current.copy(
                            hasLoaded = true,
                            isLoading = false,
                            isLoadingMore = false,
                            hasError = current.requests.isEmpty(),
                        )
                    }
                }
        }
    }

    private fun markSubmittedRequests(submittedIds: Set<Long>) {
        if (submittedIds.isEmpty()) return
        mutableState.update { state ->
            val submittedItems = state.filterStates.values
                .flatMap(RequestsFilterState::requests)
                .filter { it.id in submittedIds }
                .map { it.copy(status = PharmacyWorkStatus.OfferCreatedRequest) }
                .distinctBy(PharmacyWorkItem::id)

            val updatedStates = state.filterStates.mapValues { (filter, filterState) ->
                val updatedExisting = filterState.requests.map { item ->
                    if (item.id in submittedIds) {
                        item.copy(status = PharmacyWorkStatus.OfferCreatedRequest)
                    } else {
                        item
                    }
                }
                val withOptimisticItems = when (filter) {
                    RequestsFilter.OfferCreated -> updatedExisting + submittedItems
                    else -> updatedExisting
                }
                filterState.copy(
                    requests = withOptimisticItems
                        .filter { filter.accepts(it.status) }
                        .distinctBy(PharmacyWorkItem::id)
                        .sortedByNewest(),
                )
            }
            state.copy(filterStates = updatedStates)
        }
    }

    private fun updateFilterState(
        filter: RequestsFilter,
        transform: (RequestsFilterState) -> RequestsFilterState,
    ) {
        mutableState.update { state ->
            val current = state.filterStates[filter] ?: RequestsFilterState()
            state.copy(filterStates = state.filterStates + (filter to transform(current)))
        }
    }

    private fun sendEffect(effect: RequestsUIEffect) {
        viewModelScope.launch { mutableEffect.send(effect) }
    }

    private companion object {
        const val PAGE_SIZE = 20
    }
}

private val RequestsFilter.assignmentStatus: PharmacyRequestAssignmentStatus?
    get() = when (this) {
        RequestsFilter.All -> null
        RequestsFilter.Pending -> PharmacyRequestAssignmentStatus.Pending
        RequestsFilter.OfferCreated -> PharmacyRequestAssignmentStatus.OfferCreated
        RequestsFilter.Expired -> PharmacyRequestAssignmentStatus.Expired
    }

private fun RequestsFilter.accepts(status: PharmacyWorkStatus): Boolean = when (this) {
    RequestsFilter.All -> true
    RequestsFilter.Pending -> status == PharmacyWorkStatus.CanOffer
    RequestsFilter.OfferCreated -> status == PharmacyWorkStatus.OfferCreatedRequest
    RequestsFilter.Expired -> status == PharmacyWorkStatus.ExpiredRequest
}

private fun PharmacyRequestDomain.withLocalSubmission(
    submittedIds: Set<Long>,
): PharmacyRequestDomain = if (
    id in submittedIds && assignmentStatus == PharmacyRequestAssignmentStatus.Pending
) {
    copy(assignmentStatus = PharmacyRequestAssignmentStatus.OfferCreated)
} else {
    this
}

private fun List<PharmacyWorkItem>.sortedByNewest(): List<PharmacyWorkItem> =
    sortedWith(compareByDescending<PharmacyWorkItem> { it.createdAt.orEmpty() }.thenBy { it.stableKey })

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
    status = when (assignmentStatus) {
        PharmacyRequestAssignmentStatus.Pending -> PharmacyWorkStatus.CanOffer
        PharmacyRequestAssignmentStatus.OfferCreated -> PharmacyWorkStatus.OfferCreatedRequest
        PharmacyRequestAssignmentStatus.Expired -> PharmacyWorkStatus.ExpiredRequest
        PharmacyRequestAssignmentStatus.Unknown -> PharmacyWorkStatus.UnavailableRequest
    },
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
