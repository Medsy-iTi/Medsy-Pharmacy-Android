package com.medsy.domain.auth.repository

import com.medsy.domain.auth.model.PharmacySession
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun observeSession(): Flow<PharmacySession?>
    suspend fun clearSession()
}
