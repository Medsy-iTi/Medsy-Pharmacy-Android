package com.medsy.domain.common.preferences.usecase

import com.medsy.domain.common.preferences.repository.UserPreferencesRepository
import javax.inject.Inject

class SetReceivingNotificationsPreferenceUseCase @Inject constructor(
    private val repository: UserPreferencesRepository,
) {
    suspend operator fun invoke(isReceiving: Boolean) {
        repository.setReceivingNotifications(isReceiving)
    }
}
