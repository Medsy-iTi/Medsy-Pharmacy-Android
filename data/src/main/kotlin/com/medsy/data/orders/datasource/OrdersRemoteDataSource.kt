package com.medsy.data.orders.datasource

import com.medsy.data.orders.model.OrderDetailsResponse
import com.medsy.data.orders.model.OrderSummaryEntity
import kotlinx.coroutines.flow.Flow

interface OrdersRemoteDataSource {
    fun getOrdersStream(): Flow<List<OrderSummaryEntity>>
    suspend fun getOrderDetails(orderId: String): OrderDetailsResponse

}