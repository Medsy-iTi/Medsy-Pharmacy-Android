package com.medsy.domain.offer.usecase

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.offer.model.Offer
import com.medsy.domain.offer.repository.OfferRepository
import javax.inject.Inject

class GetOfferByIdUseCase @Inject constructor(
    private val repository: OfferRepository
) {
    suspend operator fun invoke(id: Long): MedsyResult<Offer, MedsyError> {
        val cached = repository.getCachedOffer(id)
        return if (cached != null) {
            MedsyResult.Success(cached)
        } else {
            repository.getOfferById(id)
        }
    }
}
