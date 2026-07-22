package com.medsy.presentation.orders

sealed interface OrdersUIEffect {
    data class NavigateToOrderDetails(val orderId: Long) : OrdersUIEffect
    data object OpenFilters : OrdersUIEffect
    data class ShowMessage(val messageRes: Int) : OrdersUIEffect
}
