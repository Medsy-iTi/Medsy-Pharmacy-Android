package com.medsy.domain.common.preferences.usecase

import com.medsy.domain.common.preferences.repository.UserPreferencesRepository
import javax.inject.Inject

class SetRegisteredFcmTokenUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(token: String?) {
        repository.setRegisteredFcmToken(token)
    }
}
