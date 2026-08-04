package com.medsy.domain.notifications.model

data class DeviceTokenRegistrationDomain(
    val fcmToken: String,
    val platform: String = DEFAULT_PLATFORM,
    val deviceId: String
){
    companion object {
        const val DEFAULT_PLATFORM = "ANDROID"
    }
}

data class NotificationDomain(
    val recipientId: Long,
    val category: String,
    val title: String,
    val body: String,
    val dataPayload: Map<String, String>,
    val status: String,
    val sentAt: String,
    val readAt: String?
) {
    companion object{
        const val STATUS_READ = "READ"
    }
    val isRead: Boolean
        get() = status.equals(STATUS_READ, ignoreCase = true) || readAt != null
}

data class NotificationPageDomain(
    val content: List<NotificationDomain>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalElements: Long,
    val totalPages: Int,
    val last: Boolean
)
