package com.medsy.presentation.notifications

import com.medsy.domain.notifications.model.NotificationDomain

data class NotificationsState(
    val isLoading: Boolean = false,
    val notifications: List<NotificationDomain> = emptyList(),
    val errorMsg: String? = null
)
