package com.medsy.domain.offer.repository

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.offer.model.CreateOfferRequest

interface OfferRepository {
    suspend fun createOffer(
        requestId: Long,
        request: CreateOfferRequest,
    ): EmptyMedsyResult<MedsyError.Remote>
}
