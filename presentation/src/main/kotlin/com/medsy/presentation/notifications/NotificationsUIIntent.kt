package com.medsy.presentation.notifications

sealed interface NotificationsUIIntent {
    data object LoadNotifications : NotificationsUIIntent
    data class NotificationClicked(val recipientId: Long) : NotificationsUIIntent
    data object MarkAllAsReadClicked : NotificationsUIIntent
    data object BackClicked : NotificationsUIIntent
}
