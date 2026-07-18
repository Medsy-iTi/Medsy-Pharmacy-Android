package com.medsy.data.remote.auth.api

import com.medsy.data.remote.auth.dto.RefreshRequestDto
import com.medsy.data.remote.auth.dto.RefreshTokenDto
import com.medsy.data.remote.network.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RefreshApi {
    @POST("api/v1/auth/refresh")
    suspend fun refresh(
        @Body request: RefreshRequestDto,
    ): Response<ApiResponse<RefreshTokenDto>>
}
