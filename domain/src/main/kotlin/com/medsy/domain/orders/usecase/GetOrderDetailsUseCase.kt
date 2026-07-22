package com.medsy.domain.orders.usecase

import com.medsy.domain.orders.model.OrderDetails
import com.medsy.domain.orders.repository.OrdersRepository
import javax.inject.Inject

class GetOrderDetailsUseCase @Inject constructor(
    private val repository: OrdersRepository
) {
    suspend operator fun invoke(orderId: String): OrderDetails? {
        return repository.getOrderDetails(orderId)
    }
}