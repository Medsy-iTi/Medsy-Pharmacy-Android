package com.medsy.data.orders.repository

import com.medsy.data.orders.datasource.OrdersRemoteDataSource
import com.medsy.data.orders.model.OrderSummaryEntity
import com.medsy.data.orders.model.toDomain
import com.medsy.domain.orders.model.OrderDetails
import com.medsy.domain.orders.model.OrderStatus
import com.medsy.domain.orders.model.OrderSummary
import com.medsy.domain.orders.model.PaymentMethod
import com.medsy.domain.orders.repository.OrdersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OrdersRepositoryImpl @Inject constructor(
    private val remoteDataSource: OrdersRemoteDataSource
) : OrdersRepository {

    override fun getOrders(): Flow<List<OrderSummary>> {
        return remoteDataSource.getOrdersStream().map { entities ->
            entities.map { entity: OrderSummaryEntity ->
                entity.toDomain()
            }
        }
    }

    override suspend fun getOrderDetails(orderId: String): OrderDetails {
        val response = remoteDataSource.getOrderDetails(orderId)
        return response.toDomain()
    }
}

fun OrderSummaryEntity.toDomain(): OrderSummary {
    return OrderSummary(
        id = id,
        minutesAgo = minutesAgo,
        status = when (status) {
            "NEW" -> OrderStatus.New
            "IN_PROGRESS" -> OrderStatus.InProgress
            else -> OrderStatus.New
        },
        customerName = customerName,
        customerPhone = customerPhone,
        customerAddress = customerAddress,
        total = total,
        paymentMethod = when (paymentMethod) {
            "VISA" -> PaymentMethod.Visa
            else -> PaymentMethod.Cash
        },
        paymentCardLastDigits = paymentCardLastDigits,
    )
}