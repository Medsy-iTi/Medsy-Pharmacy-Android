package com.medsy.data.notification.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.ZonedDateTime

@JsonClass(generateAdapter = true)
data class NotificationDto(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "recipientId") val recipientId: Long,
    @Json(name = "category") val category: String,
    @Json(name = "title") val title: String,
    @Json(name = "body") val body: String,
    @Json(name = "dataPayload") val dataPayload: Map<String, String>? = null,
    @Json(name = "status") val status: String,
    @Json(name = "sentAt") val sentAt: ZonedDateTime?,
    @Json(name = "readAt") val readAt: ZonedDateTime?
)

@JsonClass(generateAdapter = true)
data class PaginatedNotificationsDto(
    @Json(name = "content") val content: List<NotificationDto>,
    @Json(name = "pageNumber") val pageNumber: Int,
    @Json(name = "pageSize") val pageSize: Int,
    @Json(name = "totalElements") val totalElements: Long,
    @Json(name = "totalPages") val totalPages: Int,
    @Json(name = "last") val isLast: Boolean
)
