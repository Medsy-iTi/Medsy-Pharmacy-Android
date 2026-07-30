package com.medsy.domain.offer.usecase

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.offer.model.PaginatedOffers
import com.medsy.domain.offer.repository.OfferRepository
import javax.inject.Inject

class GetPharmacyOffersUseCase @Inject constructor(
    private val repository: OfferRepository
) {
    suspend operator fun invoke(
        pharmacyId: Long,
        page: Int = 0,
        size: Int = 20,
        sort: List<String> = listOf("createdAt,desc")
    ): MedsyResult<PaginatedOffers, MedsyError> {
        return repository.getPharmacyOffers(pharmacyId, page, size, sort)
    }
}
