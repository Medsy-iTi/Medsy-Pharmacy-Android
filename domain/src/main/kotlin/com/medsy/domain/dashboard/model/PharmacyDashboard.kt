package com.medsy.domain.dashboard.model

import com.medsy.domain.orders.model.PharmacyOrder

data class PharmacyDashboard(
    val totalOrderValue: Double,
    val totalOrders: Long,
    val requestsReceived: Long,
    val offersCreated: Long,
    val topSellingProducts: List<TopSellingProduct>,
    val recentOrders: List<PharmacyOrder>,
)

data class TopSellingProduct(
    val productId: Long,
    val productName: String,
    val imageUrl: String?,
    val totalQuantitySold: Long,
    val totalOrderValue: Double,
)

data class AiDashboardSummary(
    val period: DashboardPeriod,
    val summary: String,
    val generatedAt: String,
    val cached: Boolean,
)

enum class DashboardPeriod(val apiValue: String) {
    LastMonth("LAST_MONTH"),
}
