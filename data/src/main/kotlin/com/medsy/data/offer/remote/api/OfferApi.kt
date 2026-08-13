package com.medsy.data.offer.remote.api

import com.medsy.data.offer.remote.dto.CreateOfferRequestDto
import com.medsy.data.remote.network.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface OfferApi {
    
    @POST("api/v1/offers/requests/{requestId}")
    suspend fun createPharmacyOffer(
        @Path("requestId") requestId: Long,
        @Body request: CreateOfferRequestDto
    ): Response<ApiResponse<Any>>
}
