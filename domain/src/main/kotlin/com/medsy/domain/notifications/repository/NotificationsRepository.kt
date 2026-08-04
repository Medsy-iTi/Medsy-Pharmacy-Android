package com.medsy.domain.notifications.repository

import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.notifications.model.DeviceTokenRegistrationDomain
import com.medsy.domain.notifications.model.NotificationPageDomain

interface NotificationsRepository {

    suspend fun registerDeviceToken(
        token: DeviceTokenRegistrationDomain
    ): EmptyMedsyResult<MedsyError.Remote>

    suspend fun unregisterDeviceToken(
        fcmToken: String
    ): EmptyMedsyResult<MedsyError.Remote>

    suspend fun getNotifications(
        page: Int,
        size: Int,
        status: String? = null
    ): MedsyResult<NotificationPageDomain, MedsyError.Remote>

    suspend fun getUnreadCount(): MedsyResult<Int, MedsyError.Remote>

    suspend fun markAsRead(
        recipientId: Long
    ): EmptyMedsyResult<MedsyError.Remote>

    suspend fun markAllAsRead(): EmptyMedsyResult<MedsyError.Remote>
}
