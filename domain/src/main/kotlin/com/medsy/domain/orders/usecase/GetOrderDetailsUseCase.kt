package com.medsy.domain.orders.usecase

import com.medsy.domain.orders.model.OrderDetailsDomain
import com.medsy.domain.orders.repository.OrdersRepository
import javax.inject.Inject

class GetOrderDetailsUseCase @Inject constructor(
    private val ordersRepository: OrdersRepository
) {
    suspend operator fun invoke(orderId: Long): Result<OrderDetailsDomain?> {
        return ordersRepository.getOrderDetails(orderId)
    }
}