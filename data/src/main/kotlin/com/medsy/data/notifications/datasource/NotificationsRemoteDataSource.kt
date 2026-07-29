package com.medsy.data.notifications.datasource

import com.medsy.data.notifications.model.DeviceTokenRegistrationDto
import com.medsy.data.notifications.model.NotificationPageDto
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult

interface NotificationsRemoteDataSource {

    suspend fun registerDeviceToken(
        dto: DeviceTokenRegistrationDto
    ): EmptyMedsyResult<MedsyError.Remote>

    suspend fun unregisterDeviceToken(
        fcmToken: String
    ): EmptyMedsyResult<MedsyError.Remote>

    suspend fun getNotifications(
        page: Int,
        size: Int,
        status: String?
    ): MedsyResult<NotificationPageDto, MedsyError.Remote>

    suspend fun getUnreadCount(): MedsyResult<Int, MedsyError.Remote>

    suspend fun markAsRead(
        recipientId: Long
    ): EmptyMedsyResult<MedsyError.Remote>

    suspend fun markAllAsRead(): EmptyMedsyResult<MedsyError.Remote>
}
