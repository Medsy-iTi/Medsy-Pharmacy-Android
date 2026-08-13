package com.medsy.presentation.requests

import com.medsy.domain.orders.model.PharmacyRequest

data class RequestsUIState(
    val selectedFilter: RequestsFilter = RequestsFilter.All,
    val searchQuery: String = "",
    val filterStates: Map<RequestsFilter, RequestsFilterState> = emptyMap(),
    val isRefreshing: Boolean = false,
) {
    val selectedFilterState: RequestsFilterState
        get() = filterStates[selectedFilter] ?: RequestsFilterState()

    val isLoading: Boolean
        get() = !selectedFilterState.hasLoaded || selectedFilterState.isLoading

    val isLoadingMore: Boolean
        get() = selectedFilterState.isLoadingMore

    val hasError: Boolean
        get() = selectedFilterState.hasError

    val canLoadMore: Boolean
        get() = selectedFilterState.canLoadMore

    val visibleRequests: List<PharmacyRequest>
        get() = selectedFilterState.requests.filter {
            searchQuery.isBlank() || it.id.toString().contains(searchQuery, ignoreCase = true)
        }
}

data class RequestsFilterState(
    val requests: List<PharmacyRequest> = emptyList(),
    val nextPage: Int = 0,
    val canLoadMore: Boolean = false,
    val hasLoaded: Boolean = false,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasError: Boolean = false,
)
