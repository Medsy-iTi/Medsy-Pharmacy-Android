package com.medsy.domain.orders.repository

import com.medsy.domain.orders.model.OrderDetails
import com.medsy.domain.orders.model.OrderSummary
import kotlinx.coroutines.flow.Flow

interface OrdersRepository {
    fun getOrders(): Flow<List<OrderSummary>>
    suspend fun getOrderDetails(orderId: String): OrderDetails?
}