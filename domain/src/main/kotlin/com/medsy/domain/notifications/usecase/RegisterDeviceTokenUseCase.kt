package com.medsy.domain.notifications.usecase

import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.notifications.model.DeviceTokenRegistrationDomain
import com.medsy.domain.notifications.repository.NotificationsRepository
import javax.inject.Inject

class RegisterDeviceTokenUseCase @Inject constructor(
    private val repository: NotificationsRepository
) {
    suspend operator fun invoke(
        token: DeviceTokenRegistrationDomain
    ): EmptyMedsyResult<MedsyError.Remote> = repository.registerDeviceToken(token)
}
