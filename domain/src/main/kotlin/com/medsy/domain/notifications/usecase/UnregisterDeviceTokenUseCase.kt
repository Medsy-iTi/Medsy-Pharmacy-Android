package com.medsy.domain.notifications.usecase

import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.notifications.repository.NotificationsRepository
import javax.inject.Inject

class UnregisterDeviceTokenUseCase @Inject constructor(
    private val repository: NotificationsRepository
) {
    suspend operator fun invoke(
        fcmToken: String
    ): EmptyMedsyResult<MedsyError.Remote> = repository.unregisterDeviceToken(fcmToken)
}
