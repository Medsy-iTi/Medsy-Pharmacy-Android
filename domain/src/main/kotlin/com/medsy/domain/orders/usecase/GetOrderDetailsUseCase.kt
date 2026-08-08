package com.medsy.domain.orders.usecase

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.orders.model.PharmacyRequestDomain
import com.medsy.domain.orders.repository.OrdersRepository
import javax.inject.Inject

class GetRequestDetailsUseCase @Inject constructor(
    private val ordersRepository: OrdersRepository
) {
    suspend operator fun invoke(requestId: Long): MedsyResult<PharmacyRequestDomain, MedsyError.Remote> =
        ordersRepository.getCachedRequest(requestId)?.let { MedsyResult.Success(it) }
            ?: ordersRepository.getRequestDetails(requestId)
}
