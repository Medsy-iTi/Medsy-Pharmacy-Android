package com.medsy.data.remote.api

import com.medsy.data.orders.model.PharmacyRequestPageDto
import com.medsy.data.products.remote.dto.ProductsPageDto
import com.medsy.data.remote.network.ApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService{

    @GET("api/v1/pharmacies/requests")
    suspend fun getCurrentPharmacyRequests(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: List<String>? = null
    ): Response<ApiResponse<PharmacyRequestPageDto>>

    @POST("api/v1/offers/requests/{requestId}")
    suspend fun createOffer(
        @Path("requestId") requestId: Long,
        @retrofit2.http.Body request: com.medsy.data.orders.remote.dto.CreateOfferRequestDto
    ): Response<ApiResponse<Any>>

    @GET("api/v1/products/search")
    suspend fun searchProducts(
        @Query("keyword") keyword: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: List<String>? = null
    ): Response<ApiResponse<ProductsPageDto>>
}
