package com.medsy.data.auth.repository

import com.medsy.data.auth.mapper.toPharmacySession
import com.medsy.data.local.auth.TokenStorage
import com.medsy.domain.auth.model.AuthSession
import com.medsy.domain.auth.model.PharmacyApprovalStatus
import com.medsy.domain.auth.model.PharmacySession
import com.medsy.domain.auth.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val tokenStorage: TokenStorage,
) : SessionRepository {
    override fun observeSession(): Flow<PharmacySession?> = tokenStorage.session

    override suspend fun updateApprovalStatus(status: PharmacyApprovalStatus) {
        tokenStorage.updateApprovalStatus(status)
    }

    override suspend fun clearSession() {
        tokenStorage.clear()
    }

    override suspend fun savePharmacistSession(
        session: AuthSession,
        approvalStatus: PharmacyApprovalStatus,
    ) {
        tokenStorage.save(session.toPharmacySession(approvalStatus))
    }
}
