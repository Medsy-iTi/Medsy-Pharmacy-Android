package com.medsy.domain.auth.repository

import com.medsy.domain.auth.model.AuthSession
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult

interface AuthRepository {
    suspend fun login(
        email: String,
        password: String,
    ): MedsyResult<AuthSession, MedsyError.Remote>

}
