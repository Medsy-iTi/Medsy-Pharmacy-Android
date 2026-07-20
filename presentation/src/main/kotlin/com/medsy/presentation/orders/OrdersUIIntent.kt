package com.medsy.presentation.orders

sealed interface OrdersUIIntent {
    data class SearchQueryChanged(val query: String) : OrdersUIIntent
    data class FilterSelected(val filter: OrderFilter) : OrdersUIIntent
    data object FilterIconClicked : OrdersUIIntent
    data class OrderClicked(val orderId: String) : OrdersUIIntent
    data class AcceptOrderClicked(val orderId: String) : OrdersUIIntent
    data class PrepareOrderClicked(val orderId: String) : OrdersUIIntent
}
