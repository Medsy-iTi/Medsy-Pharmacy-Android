package com.medsy.data.auth.remote.api

import com.medsy.data.auth.remote.dto.AuthResponseDto
import com.medsy.data.auth.remote.dto.LoginRequestDto
import com.medsy.data.auth.remote.dto.RegisterRequestDto
import com.medsy.data.auth.remote.dto.VerifyOtpRequestDto
import com.medsy.data.remote.network.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthApi {
    @Headers("No-Auth: true", "Accept: */*")
    @POST("api/v1/auth/register")
    suspend fun register(
        @Body body: RegisterRequestDto,
    ): Response<ApiResponse<Any>>

    @Headers("No-Auth: true", "Accept: */*")
    @POST("api/v1/auth/verify")
    suspend fun verify(
        @Body body: VerifyOtpRequestDto,
    ): Response<ApiResponse<AuthResponseDto>>

    @Headers("No-Auth: true")
    @POST("api/v1/auth/login")
    suspend fun login(
        @Body body: LoginRequestDto,
    ): Response<ApiResponse<AuthResponseDto>>

    @POST("api/v1/auth/logout")
    suspend fun logout(
        @Body body: com.medsy.data.auth.remote.dto.LogoutRequestDto,
    ): Response<ApiResponse<Any>>
}
