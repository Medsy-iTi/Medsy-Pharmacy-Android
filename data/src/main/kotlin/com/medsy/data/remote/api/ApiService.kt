package com.medsy.data.remote.api

import com.medsy.data.orders.model.OrderDetailsDto
import com.medsy.data.orders.model.OrderPageResponseDto
import com.medsy.data.remote.network.ApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService{

    @GET("api/v1/pharmacies/requests")
    suspend fun getCurrentPharmacyRequests(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: List<String>? = null
    ): Response<ApiResponse<OrderPageResponseDto>>
}
