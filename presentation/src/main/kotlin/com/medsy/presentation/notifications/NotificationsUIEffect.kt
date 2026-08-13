package com.medsy.presentation.notifications

sealed interface NotificationsUIEffect {
    data object NavigateBack : NotificationsUIEffect
    data class NavigateToRequestDetails(val requestId: Long) : NotificationsUIEffect
    data class NavigateToOrderDetails(val orderId: Long) : NotificationsUIEffect
    data class ShowMessage(val messageRes: Int) : NotificationsUIEffect
}
