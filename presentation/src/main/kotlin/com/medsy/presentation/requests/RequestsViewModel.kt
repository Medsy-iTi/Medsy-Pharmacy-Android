package com.medsy.presentation.requests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.domain.orders.model.PharmacyRequest
import com.medsy.domain.orders.model.PharmacyRequestAssignmentStatus
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
import javax.inject.Inject

@HiltViewModel
class RequestsViewModel @Inject constructor(
    private val getCurrentPharmacyRequestsUseCase: GetCurrentPharmacyRequestsUseCase,
) : ViewModel() {
    private val mutableState = MutableStateFlow(RequestsUIState())
    val state = mutableState
        .onStart { ensureFilterLoaded(mutableState.value.selectedFilter) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), RequestsUIState())

    private val mutableEffect = Channel<RequestsUIEffect>(Channel.BUFFERED)
    val effect = mutableEffect.receiveAsFlow()
    private val loadingJobs = mutableMapOf<RequestsFilter, Job>()

    fun onIntent(intent: RequestsUIIntent) {
        when (intent) {
            is RequestsUIIntent.SearchQueryChanged -> mutableState.update { it.copy(searchQuery = intent.query) }
            is RequestsUIIntent.FilterSelected -> selectFilter(intent.filter)
            is RequestsUIIntent.RequestClicked -> sendEffect(RequestsUIEffect.NavigateToRequestDetails(intent.requestId))
            RequestsUIIntent.Refresh -> loadRequests(mutableState.value.selectedFilter, true, true)
            RequestsUIIntent.Retry -> loadRequests(mutableState.value.selectedFilter, true, false)
            RequestsUIIntent.LoadMore -> loadRequests(mutableState.value.selectedFilter, false, false)
        }
    }

    private fun selectFilter(filter: RequestsFilter) {
        mutableState.update { it.copy(selectedFilter = filter) }
        ensureFilterLoaded(filter)
    }

    private fun ensureFilterLoaded(filter: RequestsFilter) {
        val filterState = mutableState.value.filterStates[filter]
        if (filterState?.hasLoaded != true && filterState?.isLoading != true) {
            loadRequests(filter, refresh = true, userRefresh = false)
        }
    }

    private fun loadRequests(filter: RequestsFilter, refresh: Boolean, userRefresh: Boolean) {
        val currentFilterState = mutableState.value.filterStates[filter] ?: RequestsFilterState()
        if (!refresh && (currentFilterState.isLoadingMore || !currentFilterState.canLoadMore)) return
        if (userRefresh && mutableState.value.isRefreshing) return

        loadingJobs[filter]?.cancel()
        loadingJobs[filter] = viewModelScope.launch {
            val page = if (refresh) 0 else currentFilterState.nextPage
            if (userRefresh) mutableState.update { it.copy(isRefreshing = true) }
            updateFilterState(filter) { current ->
                when {
                    !refresh -> current.copy(isLoadingMore = true, hasError = false)
                    current.hasLoaded -> current.copy(hasError = false)
                    else -> current.copy(isLoading = true, isLoadingMore = false, hasError = false)
                }
            }

            getCurrentPharmacyRequestsUseCase(
                page = page,
                size = PAGE_SIZE,
                sort = listOf("id,desc"),
                assignmentStatus = filter.assignmentStatus,
            ).onSuccess { response ->
                updateFilterState(filter) { current ->
                    val combined = if (refresh) response.content
                    else (current.requests + response.content).distinctBy(PharmacyRequest::id)
                    current.copy(
                        requests = combined.sortedByDescending(PharmacyRequest::id),
                        nextPage = response.pageNumber + 1,
                        canLoadMore = !response.last,
                        hasLoaded = true,
                        isLoading = false,
                        isLoadingMore = false,
                        hasError = false,
                    )
                }
            }.onError {
                updateFilterState(filter) { current ->
                    current.copy(
                        hasLoaded = true,
                        isLoading = false,
                        isLoadingMore = false,
                        hasError = current.requests.isEmpty(),
                    )
                }
            }
            mutableState.update { it.copy(isRefreshing = false) }
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

    private companion object { const val PAGE_SIZE = 20 }
}

private val RequestsFilter.assignmentStatus: PharmacyRequestAssignmentStatus?
    get() = when (this) {
        RequestsFilter.All -> null
        RequestsFilter.Pending -> PharmacyRequestAssignmentStatus.Pending
        RequestsFilter.OfferCreated -> PharmacyRequestAssignmentStatus.OfferCreated
        RequestsFilter.Expired -> PharmacyRequestAssignmentStatus.Expired
    }
