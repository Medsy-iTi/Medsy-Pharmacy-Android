package com.medsy.domain.orders.usecase

import com.medsy.domain.orders.repository.OrdersRepository
import javax.inject.Inject

class MarkOrderDeliveredUseCase @Inject constructor(
    private val repository: OrdersRepository,
) {
    suspend operator fun invoke(orderId: Long) = repository.markOrderDelivered(orderId)
}
