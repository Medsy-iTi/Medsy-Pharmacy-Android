package com.medsy.presentation.orders


data class OrdersUIState(
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val selectedFilter: OrderFilter = OrderFilter.All,
    val orders: List<OrderSummary> = emptyList(),
) {
    val filteredOrders: List<OrderSummary>
        get() = orders.filter {
            it.matchesFilter(selectedFilter) && it.matchesQuery(searchQuery)
        }

    val newCount: Int get() = orders.count { it.status == OrderStatus.New }
    val inProgressCount: Int get() = orders.count { it.status == OrderStatus.InProgress }
}