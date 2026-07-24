package com.medsy.domain.auth.usecase

import com.medsy.domain.auth.repository.AuthRepository
import com.medsy.domain.auth.repository.SessionRepository
import com.medsy.domain.common.preferences.usecase.SetReceivingOrdersPreferenceUseCase
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionRepository: SessionRepository,
    private val setReceivingOrdersPreference: SetReceivingOrdersPreferenceUseCase,
) {
    suspend operator fun invoke() {
        val session = sessionRepository.observeSession().firstOrNull()
        if (session != null && session.refreshToken.isNotBlank()) {
            authRepository.logout(session.refreshToken)
        }
        sessionRepository.clearSession()
        setReceivingOrdersPreference(false)
    }
}
