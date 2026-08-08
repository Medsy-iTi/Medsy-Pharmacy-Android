package com.medsy.domain.orders.usecase

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.orders.model.PharmacyOrderDomain
import com.medsy.domain.orders.repository.OrdersRepository
import javax.inject.Inject

class GetPharmacyOrderDetailsUseCase @Inject constructor(
    private val repository: OrdersRepository,
) {
    suspend operator fun invoke(orderId: Long): MedsyResult<PharmacyOrderDomain, MedsyError.Remote> =
        repository.getCachedOrder(orderId)?.let { MedsyResult.Success(it) }
            ?: repository.getOrderDetails(orderId)
}
