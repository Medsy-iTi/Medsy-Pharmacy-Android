package com.medsy.data.auth.repository

import android.util.Log
import com.medsy.data.local.auth.TokenStorage
import com.medsy.data.mapper.auth.toDomain
import com.medsy.data.mapper.auth.toDto
import com.medsy.data.remote.auth.api.AuthApi
import com.medsy.data.remote.auth.dto.LoginRequestDto
import com.medsy.data.remote.auth.dto.RefreshRequestDto
import com.medsy.data.remote.auth.dto.VerifyOtpRequestDto
import com.medsy.data.remote.network.safeApiCall
import com.medsy.data.remote.network.safeEmptyRestCall
import com.medsy.domain.auth.model.AuthSession
import com.medsy.domain.auth.model.RegisterParams
import com.medsy.domain.auth.repository.AuthRepository
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.map
import com.medsy.domain.common.onSuccess
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val tokenStorage: TokenStorage,
) : AuthRepository {

    override suspend fun register(
        params: RegisterParams,
    ): EmptyMedsyResult<MedsyError.Remote> {
        Log.d("auth", "Repository: Registering user ${params.email}")
        return safeEmptyRestCall { api.register(params.toDto()) }.also {
            Log.d("auth", "Repository: Register result: $it")
        }
    }

    override suspend fun verifyOtp(
        email: String,
        otpCode: String,
    ): MedsyResult<AuthSession, MedsyError.Remote> {
        Log.d("auth", "Repository: Verifying OTP for $email")
        return safeApiCall { api.verify(VerifyOtpRequestDto(email, otpCode)) }
            .map { it.toDomain() }
            .onSuccessSave()
            .also { Log.d("auth", "Repository: Verify OTP result: $it") }
    }

    override suspend fun login(
        email: String,
        password: String,
    ): MedsyResult<AuthSession, MedsyError.Remote> {
        Log.d("auth", "Repository: Logging in user $email")
        return safeApiCall { api.login(LoginRequestDto(email, password)) }
            .map { it.toDomain() }
            .onSuccessSave()
            .also { Log.d("auth", "Repository: Login result: $it") }
    }

    override suspend fun refreshToken(): MedsyResult<AuthSession, MedsyError.Remote> {
        Log.d("auth", "Repository: Refreshing token")
        val refreshToken = tokenStorage.refreshToken()
            ?: return MedsyResult.Error(
                MedsyError.Remote.Http(statusCode = 401, serverMessage = null)
            ).also { Log.e("auth", "Repository: No refresh token found") }

        return safeApiCall { api.refresh(RefreshRequestDto(refreshToken)) }
            .map { it.toDomain() }
            .onSuccessSave()
            .also { Log.d("auth", "Repository: Refresh token result: $it") }
    }

    override suspend fun logout(): EmptyMedsyResult<MedsyError.Remote> {
        Log.d("auth", "Repository: Logging out")
        val refreshToken = tokenStorage.refreshToken()
        val result = if (refreshToken != null) {
            safeEmptyRestCall { api.logout(RefreshRequestDto(refreshToken)) }
        } else {
            MedsyResult.Success(Unit)
        }
        tokenStorage.clear()
        Log.d("auth", "Repository: Logout result: $result")
        return result
    }

    override fun observeSession(): Flow<AuthSession?> = tokenStorage.session

    override suspend fun hasValidSession(): Boolean = tokenStorage.session.value != null

    private fun MedsyResult<AuthSession, MedsyError.Remote>.onSuccessSave():
            MedsyResult<AuthSession, MedsyError.Remote> =
        onSuccess(tokenStorage::save)
}
