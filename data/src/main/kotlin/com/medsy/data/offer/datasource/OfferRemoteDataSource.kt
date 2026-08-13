package com.medsy.data.offer.datasource

import com.medsy.data.offer.remote.dto.CreateOfferRequestDto
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError

interface OfferRemoteDataSource {
    suspend fun createOffer(
        requestId: Long,
        request: CreateOfferRequestDto,
    ): EmptyMedsyResult<MedsyError.Remote>
}
