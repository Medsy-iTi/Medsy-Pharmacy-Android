package com.medsy.presentation.requests

data class RequestsUIState(
    val selectedFilter: RequestsFilter = RequestsFilter.All,
    val searchQuery: String = "",
    val filterStates: Map<RequestsFilter, RequestsFilterState> = emptyMap(),
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

    val visibleRequests: List<PharmacyWorkItem>
        get() = selectedFilterState.requests.filter { it.matchesQuery(searchQuery) }
}

data class RequestsFilterState(
    val requests: List<PharmacyWorkItem> = emptyList(),
    val nextPage: Int = 0,
    val canLoadMore: Boolean = false,
    val hasLoaded: Boolean = false,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasError: Boolean = false,
)
