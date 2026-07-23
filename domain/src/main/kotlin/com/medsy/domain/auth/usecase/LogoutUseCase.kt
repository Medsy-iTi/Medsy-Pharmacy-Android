package com.medsy.domain.auth.usecase

import com.medsy.domain.auth.repository.AuthRepository
import com.medsy.domain.auth.repository.SessionRepository
import com.medsy.domain.common.device.DeviceRepository
import com.medsy.domain.common.preferences.usecase.SetReceivingOrdersPreferenceUseCase
import com.medsy.domain.pharmacist.repository.PharmacistRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionRepository: SessionRepository,
    private val setReceivingOrdersPreference: SetReceivingOrdersPreferenceUseCase,
    private val pharmacistRepository: PharmacistRepository,
    private val deviceRepository: DeviceRepository,
) {
    suspend operator fun invoke() {
        val session = sessionRepository.observeSession().firstOrNull()
        if (session != null && session.refreshToken.isNotBlank()) {
            unregisterToken()
            authRepository.logout(session.refreshToken)
        }
        sessionRepository.clearSession()
        setReceivingOrdersPreference(false)
    }

    private suspend fun unregisterToken() {
        val fcmToken = deviceRepository.getFcmToken()
        if (fcmToken != null) {
            pharmacistRepository.unregisterDeviceToken(fcmToken)
        }
    }
}
