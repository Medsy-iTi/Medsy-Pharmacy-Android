package com.medsy.domain.auth.usecase

import com.medsy.domain.auth.repository.SessionRepository
import javax.inject.Inject

class ClearSessionUseCase @Inject constructor(
    private val repository: SessionRepository,
) {
    suspend operator fun invoke() = repository.clearSession()
}
