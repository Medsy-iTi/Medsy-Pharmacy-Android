package com.medsy.domain.orders.usecase

import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.orders.repository.OrdersRepository
import javax.inject.Inject

class MarkOrderReadyUseCase @Inject constructor(
    private val repository: OrdersRepository,
) {
    suspend operator fun invoke(orderId: Long): EmptyMedsyResult<MedsyError.Remote> =
        repository.markOrderReady(orderId)
}
