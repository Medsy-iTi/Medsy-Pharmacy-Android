package com.medsy.domain.notification.usecase

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.notification.repository.NotificationRepository
import javax.inject.Inject

class GetUnreadNotificationsCountUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(): MedsyResult<Int, MedsyError> {
        return repository.getUnreadNotificationsCount()
    }
}
