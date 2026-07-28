package com.medsy.data.notifications.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DeviceTokenRegistrationDto(
    val fcmToken: String,
    val platform: String = "ANDROID",
    val deviceId: String
)

@JsonClass(generateAdapter = true)
data class NotificationPageDto(
    val content: List<NotificationDto>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalElements: Long,
    val totalPages: Int,
    val last: Boolean
)

@JsonClass(generateAdapter = true)
data class NotificationDto(
    val recipientId: Long,
    val category: String,
    val title: String,
    val body: String,
    val dataPayload: Map<String, String>?,
    val status: String,
    val sentAt: String,
    val readAt: String?
)
