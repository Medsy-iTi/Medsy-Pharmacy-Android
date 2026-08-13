package com.medsy.data.offer.datasource

import com.medsy.data.offer.remote.api.OfferApi
import com.medsy.data.offer.remote.dto.CreateOfferRequestDto
import com.medsy.data.remote.network.safeEmptyRestCall
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import javax.inject.Inject

class OfferRemoteDataSourceImpl @Inject constructor(
    private val api: OfferApi,
) : OfferRemoteDataSource {
    override suspend fun createOffer(
        requestId: Long,
        request: CreateOfferRequestDto,
    ): EmptyMedsyResult<MedsyError.Remote> =
        safeEmptyRestCall { api.createPharmacyOffer(requestId, request) }
}
