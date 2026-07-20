package com.medsy.data.auth.repository

import com.medsy.data.auth.mapper.toDomain
import com.medsy.data.auth.remote.api.AuthApi
import com.medsy.data.auth.remote.dto.LoginRequestDto
import com.medsy.data.remote.network.safeApiCall
import com.medsy.domain.auth.model.AuthSession
import com.medsy.domain.auth.repository.AuthRepository
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
) : AuthRepository {
    override suspend fun login(
        email: String,
        password: String,
    ): MedsyResult<AuthSession, MedsyError.Remote> {
        val result = safeApiCall { api.login(LoginRequestDto(email, password)) }
            .map { it.toDomain() }
        return result
    }
}
