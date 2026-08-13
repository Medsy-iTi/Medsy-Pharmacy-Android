package com.medsy.data.remote.api

import com.medsy.data.orders.model.PharmacyOrderDto
import com.medsy.data.orders.model.PharmacyOrderPageDto
import com.medsy.data.orders.model.PharmacyRequestAssignmentDto
import com.medsy.data.orders.model.PharmacyRequestPageDto
import com.medsy.data.products.remote.dto.ProductsPageDto
import com.medsy.data.remote.network.ApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService{

    @GET("api/v1/pharmacies/requests")
    suspend fun getCurrentPharmacyRequests(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: List<String>? = null,
        @Query("status") assignmentStatus: String? = null,
    ): Response<ApiResponse<PharmacyRequestPageDto>>

    @GET("api/v1/pharmacies/requests/{requestId}")
    suspend fun getRequestById(
        @Path("requestId") requestId: Long,
    ): Response<ApiResponse<PharmacyRequestAssignmentDto>>

    @GET("api/v1/orders/pharmacy/{pharmacyId}")
    suspend fun getPharmacyOrders(
        @Path("pharmacyId") pharmacyId: Long,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: List<String>? = null,
        @Query("status") status: String? = null,
    ): Response<ApiResponse<PharmacyOrderPageDto>>

    @GET("api/v1/orders/{orderId}")
    suspend fun getOrderById(
        @Path("orderId") orderId: Long,
    ): Response<ApiResponse<PharmacyOrderDto>>

    @PATCH("api/v1/pharmacists/orders/{orderId}/ready")
    suspend fun markOrderReady(
        @Path("orderId") orderId: Long,
    ): Response<ApiResponse<Any>>

    @PATCH("api/v1/pharmacists/orders/{orderId}/out-for-delivery")
    suspend fun markOrderOutForDelivery(
        @Path("orderId") orderId: Long,
    ): Response<ApiResponse<Any>>

    @PATCH("api/v1/pharmacists/orders/{orderId}/delivered")
    suspend fun markOrderDelivered(
        @Path("orderId") orderId: Long,
    ): Response<ApiResponse<Any>>

    @GET("api/v1/products/search")
    suspend fun searchProducts(
        @Query("keyword") keyword: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: List<String>? = null
    ): Response<ApiResponse<ProductsPageDto>>
}
