package com.medsy.domain.notification.repository

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.notification.model.PaginatedNotifications

interface NotificationRepository {
    suspend fun getNotifications(
        status: String?,
        page: Int,
        size: Int,
        sort: List<String>
    ): MedsyResult<PaginatedNotifications, MedsyError>

    suspend fun getUnreadNotificationsCount(): MedsyResult<Int, MedsyError>

    suspend fun markNotificationAsRead(id: Long): MedsyResult<String, MedsyError>

    suspend fun markAllNotificationsAsRead(): MedsyResult<String, MedsyError>
}
