package com.medsy.domain.auth.repository

import com.medsy.domain.auth.model.AuthSession
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun observeSession(): Flow<AuthSession?>
    suspend fun clearSession()
}
