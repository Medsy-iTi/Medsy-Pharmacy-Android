package com.medsy.domain.auth.repository

import com.medsy.domain.auth.model.AuthSession
import com.medsy.domain.auth.model.RegisterParams
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult

interface AuthRepository {
    suspend fun register(params: RegisterParams): EmptyMedsyResult<MedsyError.Remote>

    suspend fun verifyOtp(
        email: String,
        otpCode: String,
    ): MedsyResult<AuthSession, MedsyError.Remote>

    suspend fun login(
        email: String,
        password: String,
    ): MedsyResult<AuthSession, MedsyError.Remote>

    suspend fun logout(refreshToken: String): EmptyMedsyResult<MedsyError.Remote>
}
