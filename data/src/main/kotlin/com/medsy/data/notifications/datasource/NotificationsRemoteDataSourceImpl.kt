package com.medsy.data.notifications.datasource

import com.medsy.data.notifications.model.DeviceTokenRegistrationDto
import com.medsy.data.notifications.model.NotificationPageDto
import com.medsy.data.notifications.remote.NotificationsApi
import com.medsy.data.remote.network.safeApiCall
import com.medsy.data.remote.network.safeEmptyRestCall
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import javax.inject.Inject

class NotificationsRemoteDataSourceImpl @Inject constructor(
    private val api: NotificationsApi
) : NotificationsRemoteDataSource {

    override suspend fun registerDeviceToken(
        dto: DeviceTokenRegistrationDto
    ): EmptyMedsyResult<MedsyError.Remote> =
        safeEmptyRestCall { api.registerDeviceToken(dto) }

    override suspend fun unregisterDeviceToken(
        fcmToken: String
    ): EmptyMedsyResult<MedsyError.Remote> =
        safeEmptyRestCall { api.unregisterDeviceToken(fcmToken) }

    override suspend fun getNotifications(
        page: Int,
        size: Int,
        status: String?
    ): MedsyResult<NotificationPageDto, MedsyError.Remote> =
        safeApiCall { api.getNotifications(status, page, size) }

    override suspend fun getUnreadCount(): MedsyResult<Int, MedsyError.Remote> =
        safeApiCall { api.getUnreadNotificationCount() }

    override suspend fun markAsRead(
        recipientId: Long
    ): EmptyMedsyResult<MedsyError.Remote> =
        safeEmptyRestCall { api.markNotificationAsRead(recipientId) }

    override suspend fun markAllAsRead(): EmptyMedsyResult<MedsyError.Remote> =
        safeEmptyRestCall { api.markAllNotificationsAsRead() }
}
