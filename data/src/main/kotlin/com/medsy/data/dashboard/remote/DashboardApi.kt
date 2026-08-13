package com.medsy.data.dashboard.remote

import com.medsy.data.remote.network.ApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface DashboardApi {
    @GET("api/v1/pharmacies/dashboard")
    suspend fun getDashboard(
        @Query("period") period: String,
    ): Response<ApiResponse<PharmacyDashboardDto>>
}
