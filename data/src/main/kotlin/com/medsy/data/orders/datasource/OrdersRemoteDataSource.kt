package com.medsy.data.orders.datasource

import com.medsy.data.orders.model.OrderDetailsDto
import com.medsy.data.orders.model.OrderPageResponseDto

interface OrdersRemoteDataSource {
    suspend fun getCurrentPharmacyRequests(
        page: Int,
        size: Int,
        sort: List<String>?
    ): OrderPageResponseDto
}