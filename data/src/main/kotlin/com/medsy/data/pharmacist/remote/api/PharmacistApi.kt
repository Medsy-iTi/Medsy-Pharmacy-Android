package com.medsy.data.pharmacist.remote.api

import com.medsy.data.pharmacist.remote.dto.PharmacistDto
import com.medsy.data.remote.network.ApiResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.PUT
import retrofit2.http.Body

interface PharmacistApi {
    @GET("api/v1/pharmacists/me")
    suspend fun getCurrentPharmacist(): Response<ApiResponse<PharmacistDto>>

    @DELETE("api/v1/pharmacists/{id}/pharmacy/{pharmacyId}")
    suspend fun removePharmacistFromPharmacy(
        @Path("id") pharmacistId: Long,
        @Path("pharmacyId") pharmacyId: Long
    ): Response<ApiResponse<String>>

    @DELETE("api/v1/pharmacists/me/pharmacy/{pharmacyId}")
    suspend fun leavePharmacy(
        @Path("pharmacyId") pharmacyId: Long
    ): Response<ApiResponse<String>>

    @PUT("api/v1/pharmacists/me")
    suspend fun updateCurrentPharmacist(
        @Body request: com.medsy.data.pharmacist.remote.dto.UpdatePharmacistRequestDto
    ): Response<ApiResponse<PharmacistDto>>
}
