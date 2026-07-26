package com.medsy.data.notifications.mapper

import com.medsy.data.notifications.model.DeviceTokenRegistrationDto
import com.medsy.data.notifications.model.NotificationDto
import com.medsy.data.notifications.model.NotificationPageDto
import com.medsy.domain.notifications.model.DeviceTokenRegistrationDomain
import com.medsy.domain.notifications.model.NotificationDomain
import com.medsy.domain.notifications.model.NotificationPageDomain

fun DeviceTokenRegistrationDomain.toDto(): DeviceTokenRegistrationDto {
    return DeviceTokenRegistrationDto(
        fcmToken = fcmToken,
        platform = platform,
        deviceId = deviceId
    )
}

fun NotificationDto.toDomain(): NotificationDomain {
    return NotificationDomain(
        recipientId = recipientId,
        category = category,
        title = title,
        body = body,
        dataPayload = dataPayload ?: emptyMap(),
        status = status,
        sentAt = sentAt,
        readAt = readAt
    )
}

fun NotificationPageDto.toDomain(): NotificationPageDomain {
    return NotificationPageDomain(
        content = content.map { it.toDomain() },
        pageNumber = pageNumber,
        pageSize = pageSize,
        totalElements = totalElements,
        totalPages = totalPages,
        last = last
    )
}
