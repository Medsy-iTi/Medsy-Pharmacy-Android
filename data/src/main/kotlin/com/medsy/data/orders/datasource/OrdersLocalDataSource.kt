package com.medsy.data.orders.datasource

import com.medsy.data.orders.model.OrderSummaryEntity
import kotlinx.coroutines.flow.Flow

interface OrdersLocalDataSource {
    fun getOrdersStream(): Flow<List<OrderSummaryEntity>>
}