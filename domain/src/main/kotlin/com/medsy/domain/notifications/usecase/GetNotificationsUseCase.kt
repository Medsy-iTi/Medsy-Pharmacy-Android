package com.medsy.domain.notifications.usecase

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.notifications.model.NotificationPageDomain
import com.medsy.domain.notifications.repository.NotificationsRepository
import javax.inject.Inject

class GetNotificationsUseCase @Inject constructor(
    private val repository: NotificationsRepository
) {
    suspend operator fun invoke(
        page: Int,
        size: Int,
        status: String? = null
    ): MedsyResult<NotificationPageDomain, MedsyError.Remote> =
        repository.getNotifications(page, size, status)
}
