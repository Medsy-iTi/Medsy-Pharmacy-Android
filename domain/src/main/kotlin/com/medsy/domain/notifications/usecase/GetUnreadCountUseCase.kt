package com.medsy.domain.notifications.usecase

import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.notifications.repository.NotificationsRepository
import javax.inject.Inject

class GetUnreadCountUseCase @Inject constructor(
    private val repository: NotificationsRepository
) {
    suspend operator fun invoke(): MedsyResult<Int, MedsyError.Remote> =
        repository.getUnreadCount()
}
