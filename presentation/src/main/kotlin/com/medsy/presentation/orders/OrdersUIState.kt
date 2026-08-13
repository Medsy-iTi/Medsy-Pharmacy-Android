package com.medsy.presentation.orders

import com.medsy.domain.orders.model.PharmacyOrder

data class OrdersUIState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasError: Boolean = false,
    val selectedFilter: OrdersFilter = OrdersFilter.All,
    val searchQuery: String = "",
    val orders: List<PharmacyOrder> = emptyList(),
    val canLoadMore: Boolean = false,
) {
    val visibleOrders: List<PharmacyOrder>
        get() = orders
            .filter { order ->
                when (selectedFilter) {
                    OrdersFilter.All -> true
                    OrdersFilter.WaitingForPatient -> order.status.isWaitingForPatient
                    OrdersFilter.InProgress -> order.status.isInProgress
                    OrdersFilter.Completed -> order.status.isCompleted
                }
            }
            .filter { order ->
                searchQuery.isBlank() || order.id.toString().contains(searchQuery, true) ||
                    order.customerName?.contains(searchQuery, true) == true
            }
}
