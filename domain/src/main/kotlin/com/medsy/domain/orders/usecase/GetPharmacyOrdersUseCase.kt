package com.medsy.domain.orders.usecase

import com.medsy.domain.orders.model.PharmacyOrderStatus
import com.medsy.domain.orders.repository.OrdersRepository
import javax.inject.Inject

class GetPharmacyOrdersUseCase @Inject constructor(
    private val repository: OrdersRepository,
) {
    suspend operator fun invoke(
        pharmacyId: Long,
        page: Int,
        size: Int = 20,
        sort: List<String>? = listOf("id,desc"),
        statuses: List<PharmacyOrderStatus>? = null,
    ) = repository.getPharmacyOrders(pharmacyId, page, size, sort, statuses)
}
