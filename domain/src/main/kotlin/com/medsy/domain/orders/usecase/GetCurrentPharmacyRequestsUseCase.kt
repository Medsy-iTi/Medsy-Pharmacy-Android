package com.medsy.domain.orders.usecase

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.orders.model.PharmacyRequestPage
import com.medsy.domain.orders.model.PharmacyRequestAssignmentStatus
import com.medsy.domain.orders.repository.OrdersRepository
import javax.inject.Inject

class GetCurrentPharmacyRequestsUseCase @Inject constructor(
    private val ordersRepository: OrdersRepository
) {
    suspend operator fun invoke(
        page: Int,
        size: Int,
        sort: List<String>? = null,
        assignmentStatus: PharmacyRequestAssignmentStatus? = null,
    ): MedsyResult<PharmacyRequestPage, MedsyError.Remote> =
        ordersRepository.getCurrentPharmacyRequests(page, size, sort, assignmentStatus)
}
