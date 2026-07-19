package com.medsy.data.remote.pharmacy.api

import com.medsy.data.remote.network.ApiResponse
import com.medsy.data.remote.pharmacy.dto.RegisterPharmacyRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface PharmacyApi {
    @Headers("Accept: */*")
    @POST("api/v1/pharmacies")
    suspend fun registerPharmacy(
        @Body request: RegisterPharmacyRequestDto
    ): Response<ApiResponse<Any>>
}