package com.medsy.data.notifications.repository

import com.medsy.data.notifications.datasource.NotificationsRemoteDataSource
import com.medsy.data.notifications.mapper.toDomain
import com.medsy.data.notifications.mapper.toDto
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.map
import com.medsy.domain.notifications.model.DeviceTokenRegistrationDomain
import com.medsy.domain.notifications.model.NotificationPageDomain
import com.medsy.domain.notifications.repository.NotificationsRepository
import javax.inject.Inject

class NotificationsRepositoryImpl @Inject constructor(
    private val remoteDataSource: NotificationsRemoteDataSource
) : NotificationsRepository {

    override suspend fun registerDeviceToken(
        token: DeviceTokenRegistrationDomain
    ): EmptyMedsyResult<MedsyError.Remote> =
        remoteDataSource.registerDeviceToken(token.toDto())

    override suspend fun unregisterDeviceToken(
        fcmToken: String
    ): EmptyMedsyResult<MedsyError.Remote> =
        remoteDataSource.unregisterDeviceToken(fcmToken)

    override suspend fun getNotifications(
        page: Int,
        size: Int,
        status: String?
    ): MedsyResult<NotificationPageDomain, MedsyError.Remote> =
        remoteDataSource.getNotifications(page, size, status).map { it.toDomain() }

    override suspend fun getUnreadCount(): MedsyResult<Int, MedsyError.Remote> =
        remoteDataSource.getUnreadCount()

    override suspend fun markAsRead(
        recipientId: Long
    ): EmptyMedsyResult<MedsyError.Remote> =
        remoteDataSource.markAsRead(recipientId)

    override suspend fun markAllAsRead(): EmptyMedsyResult<MedsyError.Remote> =
        remoteDataSource.markAllAsRead()
}
