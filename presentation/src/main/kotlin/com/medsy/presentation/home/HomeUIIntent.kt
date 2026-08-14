package com.medsy.presentation.home

sealed interface HomeUIIntent {
    data object Refresh : HomeUIIntent
    data class OnOrderClicked(val orderId: Long) : HomeUIIntent
    data object OnViewAllOrdersClicked : HomeUIIntent
    data object OnNotificationsClicked : HomeUIIntent
    data object RetryAiSummary : HomeUIIntent
}
