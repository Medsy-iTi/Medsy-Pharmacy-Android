package com.medsy.domain.orders.usecase

import com.medsy.domain.orders.model.OrderPageDomain
import com.medsy.domain.orders.repository.OrdersRepository
import javax.inject.Inject

class GetCurrentPharmacyRequestsUseCase @Inject constructor(
    private val ordersRepository: OrdersRepository
) {
    suspend operator fun invoke(
        page: Int,
        size: Int,
        sort: List<String>? = null
    ): Result<OrderPageDomain> {
        return ordersRepository.getCurrentPharmacyRequests(page, size, sort)
    }
}