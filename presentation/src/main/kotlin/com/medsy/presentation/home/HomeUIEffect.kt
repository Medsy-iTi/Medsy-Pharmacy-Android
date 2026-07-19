package com.medsy.presentation.home



sealed interface HomeUIEffect {
    data object NavigateToViewAllOrders : HomeUIEffect
    data class NavigateToOrderDetails(val orderId: String) : HomeUIEffect
}