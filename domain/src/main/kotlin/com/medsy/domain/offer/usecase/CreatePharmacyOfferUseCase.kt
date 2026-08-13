package com.medsy.domain.offer.usecase

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.offer.model.CreateOfferRequest
import com.medsy.domain.offer.repository.OfferRepository
import javax.inject.Inject

class CreatePharmacyOfferUseCase @Inject constructor(
    private val repository: OfferRepository
) {
    suspend operator fun invoke(
        requestId: Long,
        request: CreateOfferRequest,
    ): EmptyMedsyResult<MedsyError.Remote> {
        return repository.createOffer(requestId, request)
    }
}
