package com.medsy.domain.orders.usecase

import com.medsy.domain.orders.repository.OrdersRepository
import javax.inject.Inject

class MarkOrderOutForDeliveryUseCase @Inject constructor(
    private val repository: OrdersRepository,
) {
    suspend operator fun invoke(orderId: Long) = repository.markOrderOutForDelivery(orderId)
}
