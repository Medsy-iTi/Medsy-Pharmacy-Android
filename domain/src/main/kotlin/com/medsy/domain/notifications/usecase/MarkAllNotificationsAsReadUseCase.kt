package com.medsy.domain.notifications.usecase

import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.notifications.repository.NotificationsRepository
import javax.inject.Inject

class MarkAllNotificationsAsReadUseCase @Inject constructor(
    private val repository: NotificationsRepository
) {
    suspend operator fun invoke(): EmptyMedsyResult<MedsyError.Remote> = repository.markAllAsRead()
}
