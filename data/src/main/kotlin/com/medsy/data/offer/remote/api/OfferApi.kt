package com.medsy.data.offer.remote.api

import com.medsy.data.offer.remote.dto.CreateOfferRequestDto
import com.medsy.data.offer.remote.dto.OfferDto
import com.medsy.data.offer.remote.dto.PaginatedOffersDto
import com.medsy.data.remote.network.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface OfferApi {
    
    @POST("api/v1/offers/requests/{requestId}")
    suspend fun createPharmacyOffer(
        @Path("requestId") requestId: Long,
        @Body request: CreateOfferRequestDto
    ): Response<ApiResponse<OfferDto>>

    @GET("api/v1/offers/{id}")
    suspend fun getOfferById(
        @Path("id") id: Long
    ): Response<ApiResponse<OfferDto>>

    @GET("api/v1/offers/pharmacy/{pharmacyId}")
    suspend fun getPharmacyOffers(
        @Path("pharmacyId") pharmacyId: Long,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: List<String>
    ): Response<ApiResponse<PaginatedOffersDto>>
}
