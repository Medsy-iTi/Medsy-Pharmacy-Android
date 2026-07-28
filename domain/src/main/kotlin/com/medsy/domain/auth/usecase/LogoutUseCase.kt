package com.medsy.domain.auth.usecase

import com.medsy.domain.auth.repository.AuthRepository
import com.medsy.domain.auth.repository.SessionRepository
import com.medsy.domain.common.preferences.usecase.ObserveUserPreferencesUseCase
import com.medsy.domain.common.preferences.usecase.SetReceivingOrdersPreferenceUseCase
import com.medsy.domain.common.preferences.usecase.SetRegisteredFcmTokenUseCase
import com.medsy.domain.notifications.usecase.UnregisterDeviceTokenUseCase
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionRepository: SessionRepository,
    private val setReceivingOrdersPreference: SetReceivingOrdersPreferenceUseCase,
    private val unregisterDeviceToken: UnregisterDeviceTokenUseCase,
    private val observePreferences: ObserveUserPreferencesUseCase,
    private val setRegisteredFcmToken: SetRegisteredFcmTokenUseCase,
) {
    suspend operator fun invoke() {
        val prefs = observePreferences().firstOrNull()
        val fcmToken = prefs?.registeredFcmToken
        if (!fcmToken.isNullOrBlank()) {
            unregisterDeviceToken(fcmToken)
            setRegisteredFcmToken(null)
        }
        val session = sessionRepository.observeSession().firstOrNull()
        if (session != null && session.refreshToken.isNotBlank()) {
            authRepository.logout(session.refreshToken)
        }
        sessionRepository.clearSession()
        setReceivingOrdersPreference(false)
    }
}
