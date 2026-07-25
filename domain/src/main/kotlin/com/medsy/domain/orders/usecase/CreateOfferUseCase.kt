package com.medsy.domain.orders.usecase

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.orders.repository.OrdersRepository
import javax.inject.Inject

class CreateOfferUseCase @Inject constructor(
    private val repository: OrdersRepository
) {
    suspend operator fun invoke(requestId: Long, items: List<Pair<Long, Long>>): MedsyResult<Unit, MedsyError.Remote> {
        return repository.createOffer(requestId, items)
    }
}
