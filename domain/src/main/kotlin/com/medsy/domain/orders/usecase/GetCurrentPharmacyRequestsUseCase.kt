package com.medsy.domain.orders.usecase

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.orders.model.PharmacyRequestPageDomain
import com.medsy.domain.orders.repository.RequestsRepository
import javax.inject.Inject

class GetCurrentPharmacyRequestsUseCase @Inject constructor(
    private val requestsRepository: RequestsRepository
) {
    suspend operator fun invoke(
        page: Int,
        size: Int,
        sort: List<String>? = null
    ): MedsyResult<PharmacyRequestPageDomain, MedsyError.Remote> =
        requestsRepository.getCurrentPharmacyRequests(page, size, sort)
}