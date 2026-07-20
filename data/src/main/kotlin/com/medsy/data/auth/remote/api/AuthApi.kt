package com.medsy.data.auth.remote.api

import com.medsy.data.auth.remote.dto.AuthResponseDto
import com.medsy.data.auth.remote.dto.LoginRequestDto
import com.medsy.data.remote.network.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthApi {
    @Headers("No-Auth: true")
    @POST("api/v1/auth/login")
    suspend fun login(
        @Body body: LoginRequestDto,
    ): Response<ApiResponse<AuthResponseDto>>
}
