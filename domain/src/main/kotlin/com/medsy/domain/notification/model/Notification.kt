package com.medsy.domain.notification.model

import java.time.ZonedDateTime

data class Notification(
    val id: Long = 0L, // Backend API doesn't list id in the example, but usually they have one. Let's add it just in case, or we'll just use recipientId if needed. Actually it should have an ID for read status.
    val recipientId: Long,
    val category: String,
    val title: String,
    val body: String,
    val dataPayload: Map<String, String>,
    val status: String,
    val sentAt: ZonedDateTime?,
    val readAt: ZonedDateTime?
)

data class PaginatedNotifications(
    val content: List<Notification>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalElements: Long,
    val totalPages: Int,
    val isLast: Boolean
)
