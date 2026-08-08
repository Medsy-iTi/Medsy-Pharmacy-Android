package com.medsy.presentation.requests

data class RequestsUIState(
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val hasError: Boolean = false,
    val canLoadMore: Boolean = false,
    val searchQuery: String = "",
    val requests: List<PharmacyWorkItem> = emptyList(),
) {
    val visibleRequests: List<PharmacyWorkItem>
        get() = requests.filter { it.matchesQuery(searchQuery) }
}
