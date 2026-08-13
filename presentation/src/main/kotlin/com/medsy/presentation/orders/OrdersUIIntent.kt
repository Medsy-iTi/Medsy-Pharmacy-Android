package com.medsy.presentation.orders


sealed interface OrdersUIIntent {
    data class SearchQueryChanged(val query: String) : OrdersUIIntent
    data class FilterSelected(val filter: OrdersFilter) : OrdersUIIntent
    data class OrderClicked(val orderId: Long) : OrdersUIIntent
    data object Refresh : OrdersUIIntent
    data object Retry : OrdersUIIntent
    data object LoadMore : OrdersUIIntent
}
