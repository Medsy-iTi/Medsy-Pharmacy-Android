package com.medsy.data.auth.repository

import com.medsy.data.auth.mapper.toDomain
import com.medsy.data.auth.mapper.toDto
import com.medsy.data.auth.remote.api.AuthApi
import com.medsy.data.auth.remote.dto.LoginRequestDto
import com.medsy.data.auth.remote.dto.VerifyOtpRequestDto
import com.medsy.data.remote.network.safeApiCall
import com.medsy.data.remote.network.safeEmptyRestCall
import com.medsy.domain.auth.model.AuthSession
import com.medsy.domain.auth.model.RegisterParams
import com.medsy.domain.auth.repository.AuthRepository
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
) : AuthRepository {
    override suspend fun register(
        params: RegisterParams,
    ): EmptyMedsyResult<MedsyError.Remote> =
        safeEmptyRestCall { api.register(params.toDto()) }

    override suspend fun verifyOtp(
        email: String,
        otpCode: String,
    ): MedsyResult<AuthSession, MedsyError.Remote> =
        safeApiCall { api.verify(VerifyOtpRequestDto(email.trim(), otpCode)) }
            .map { it.toDomain() }

    override suspend fun login(
        email: String,
        password: String,
    ): MedsyResult<AuthSession, MedsyError.Remote> =
        safeApiCall { api.login(LoginRequestDto(email.trim(), password)) }
            .map { it.toDomain() }
}
