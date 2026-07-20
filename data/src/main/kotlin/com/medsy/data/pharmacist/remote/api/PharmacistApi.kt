package com.medsy.data.pharmacist.remote.api

import com.medsy.data.pharmacist.remote.dto.PharmacistDto
import com.medsy.data.remote.network.ApiResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

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
}
