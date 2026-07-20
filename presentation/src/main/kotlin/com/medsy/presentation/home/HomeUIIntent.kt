package com.medsy.presentation.home

sealed interface HomeUIIntent {
    data object Refresh : HomeUIIntent
    data class OnOrderClicked(val orderId: String) : HomeUIIntent
    data object OnViewAllOrdersClicked : HomeUIIntent
    data object OnNotificationsClicked : HomeUIIntent
}
