package com.medsy.presentation.orders


data class RequestsUIState(
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val selectedFilter: RequestFilter = RequestFilter.All,
    val orders: List<RequestSummary> = emptyList(),
) {
    val filteredOrders: List<RequestSummary>
        get() = orders.filter {
            it.matchesFilter(selectedFilter) && it.matchesQuery(searchQuery)
        }

    val newCount: Int get() = orders.count { it.status == RequestStatus.New || it.status == RequestStatus.Searching }
    val inProgressCount: Int get() = orders.count { it.status == RequestStatus.InProgress }
}