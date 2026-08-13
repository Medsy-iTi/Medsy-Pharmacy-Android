package com.medsy.data.dashboard.mapper

import com.medsy.data.dashboard.remote.PharmacyDashboardDto
import com.medsy.data.orders.mapper.toDomain
import com.medsy.domain.dashboard.model.PharmacyDashboard
import com.medsy.domain.dashboard.model.TopSellingProduct

fun PharmacyDashboardDto.toDomain(): PharmacyDashboard = PharmacyDashboard(
    totalOrderValue = totalRevenue ?: 0.0,
    totalOrders = totalOrders ?: 0,
    requestsReceived = requestsReceived ?: 0,
    offersCreated = offersCreated ?: 0,
    topSellingProducts = topSellingProducts.map { product ->
        TopSellingProduct(
            productId = product.productId ?: 0,
            productName = product.productName.orEmpty(),
            imageUrl = product.imageUrl,
            totalQuantitySold = product.totalQuantitySold ?: 0,
            totalOrderValue = product.totalRevenue ?: 0.0,
        )
    },
    recentOrders = recentOrders.map { it.toDomain() },
)
