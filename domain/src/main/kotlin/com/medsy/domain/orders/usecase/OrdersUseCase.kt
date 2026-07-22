package com.medsy.domain.orders.usecase

import com.medsy.domain.orders.model.OrderSummary
import com.medsy.domain.orders.repository.OrdersRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOrdersUseCase @Inject constructor(
    private val ordersRepository: OrdersRepository
) {
    operator fun invoke(): Flow<List<OrderSummary>> {
        return ordersRepository.getOrders()
    }
}