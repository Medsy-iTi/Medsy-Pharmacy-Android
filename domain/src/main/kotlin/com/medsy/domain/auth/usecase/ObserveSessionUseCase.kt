package com.medsy.domain.auth.usecase

import com.medsy.domain.auth.model.PharmacySession
import com.medsy.domain.auth.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSessionUseCase @Inject constructor(
    private val repository: SessionRepository,
) {
    operator fun invoke(): Flow<PharmacySession?> = repository.observeSession()
}
