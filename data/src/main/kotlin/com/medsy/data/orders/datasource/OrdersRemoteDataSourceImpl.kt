package com.medsy.data.orders.datasource

import com.medsy.data.orders.model.OrderDetailsResponse
import com.medsy.data.orders.model.OrderItemResponse
import com.medsy.data.orders.model.OrderSummaryEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class OrdersRemoteDataSourceImpl @Inject constructor() : OrdersRemoteDataSource {
    override fun getOrdersStream(): Flow<List<OrderSummaryEntity>> = flow {
        delay(300) // simulated data fetch
        emit(
            listOf(
                OrderSummaryEntity(
                    id = "1258",
                    minutesAgo = 5,
                    status = "NEW",
                    customerName = "Omar Ramadan",
                    customerPhone = "011 522 671 25",
                    customerAddress = "Nile St, Maadi, Cairo",
                    total = 165.0,
                    paymentMethod = "CASH",
                    paymentCardLastDigits = null,
                ),
                OrderSummaryEntity(
                    id = "1257",
                    minutesAgo = 15,
                    status = "IN_PROGRESS",
                    customerName = "Mennatallah Mahmoud",
                    customerPhone = "010 9876 5432",
                    customerAddress = "Nile St, Maadi",
                    total = 230.0,
                    paymentMethod = "VISA",
                    paymentCardLastDigits = "3456",
                ),
            )
        )
    }

    override suspend fun getOrderDetails(orderId: String): OrderDetailsResponse {
        delay(300) // simulated network delay

        return OrderDetailsResponse(
            id = orderId,
            minutesAgo = 5,
            status = "NEW",
            customerName = "Omar Ramadan",
            customerPhone = "011 522 671 25",
            customerAddress = "Nile St, Maadi, Cairo",
            total = 165.0,
            paymentMethod = "CASH",
            paymentCardLastDigits = null,
            items = listOf(
                OrderItemResponse(
                    id = "item_1",
                    name = "Panadol Extra",
                    quantity = 2,
                    price = 65.0,
                    imageUrl = null
                ),
                OrderItemResponse(
                    id = "item_2",
                    name = "Vitamin C",
                    quantity = 1,
                    price = 35.0,
                    imageUrl = null
                )
            )
        )
    }
}