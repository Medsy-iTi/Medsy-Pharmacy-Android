package com.medsy.data.offer.repository

import com.medsy.data.offer.mapper.toDto
import com.medsy.data.offer.datasource.OfferRemoteDataSource
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.offer.model.CreateOfferRequest
import com.medsy.domain.offer.repository.OfferRepository
import javax.inject.Inject

class OfferRepositoryImpl @Inject constructor(
    private val remoteDataSource: OfferRemoteDataSource,
) : OfferRepository {

    override suspend fun createOffer(
        requestId: Long,
        request: CreateOfferRequest
    ): EmptyMedsyResult<MedsyError.Remote> =
        remoteDataSource.createOffer(requestId, request.toDto())
}
