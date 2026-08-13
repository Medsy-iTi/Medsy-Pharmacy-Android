package com.medsy.data.dashboard.remote

import com.medsy.data.orders.model.PharmacyOrderDto
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PharmacyDashboardDto(
    val totalRevenue: Double? = null,
    val totalOrders: Long? = null,
    val requestsReceived: Long? = null,
    val offersCreated: Long? = null,
    val topSellingProducts: List<TopSellingProductDto> = emptyList(),
    val recentOrders: List<PharmacyOrderDto> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class TopSellingProductDto(
    val productId: Long? = null,
    val productName: String? = null,
    val imageUrl: String? = null,
    val totalQuantitySold: Long? = null,
    val totalRevenue: Double? = null,
)
