package com.medsy.domain.auth.repository

import com.medsy.domain.auth.model.AuthSession
import com.medsy.domain.auth.model.PharmacyApprovalStatus
import com.medsy.domain.auth.model.PharmacySession
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun observeSession(): Flow<PharmacySession?>
    suspend fun updateApprovalStatus(status: PharmacyApprovalStatus)
    suspend fun clearSession()

    suspend fun savePharmacistSession(
        session: AuthSession,
        approvalStatus: PharmacyApprovalStatus,
    )
}
