package com.medsy.data.repository

import com.medsy.data.orders.datasource.OrdersLocalDataSource
import com.medsy.data.orders.model.OrderSummaryEntity
import com.medsy.domain.orders.model.OrderStatus
import com.medsy.domain.orders.model.OrderSummary
import com.medsy.domain.orders.model.PaymentMethod
import com.medsy.domain.orders.repository.OrdersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.collections.map

class OrdersRepositoryImpl @Inject constructor(
    private val localDataSource: OrdersLocalDataSource
) : OrdersRepository {

    override fun getOrders(): Flow<List<OrderSummary>> {
        return localDataSource.getOrdersStream().map { entities ->
            entities.map { entity: OrderSummaryEntity ->
                entity.toDomain()
            }
        }
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