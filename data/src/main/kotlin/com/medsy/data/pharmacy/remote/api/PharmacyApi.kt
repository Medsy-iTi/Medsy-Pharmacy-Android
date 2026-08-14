package com.medsy.data.pharmacy.remote.api

import com.medsy.data.pharmacy.remote.dto.PharmacyMineDto
import com.medsy.data.pharmacy.remote.dto.PharmacyResponseDto
import com.medsy.data.remote.network.ApiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface PharmacyApi {
    @GET("api/v1/pharmacies/mine")
    suspend fun getMyPharmacy(): Response<ApiResponse<PharmacyMineDto>>

    @Multipart
    @POST("api/v1/pharmacies")
    suspend fun createPharmacy(
        @Part("pharmacyRequest") pharmacyRequest: RequestBody,
        @Part license: MultipartBody.Part,
    ): Response<ApiResponse<PharmacyResponseDto>>

    @retrofit2.http.PUT("api/v1/pharmacies/{id}")
    suspend fun updatePharmacy(
        @retrofit2.http.Path("id") id: Long,
        @retrofit2.http.Body request: com.medsy.data.pharmacy.remote.dto.UpdatePharmacyRequestDto
    ): Response<ApiResponse<PharmacyResponseDto>>
}
