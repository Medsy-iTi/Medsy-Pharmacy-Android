package com.medsy.data.offer.repository

import com.medsy.data.offer.mapper.toDomain
import com.medsy.data.offer.mapper.toDto
import com.medsy.data.offer.remote.api.OfferApi
import com.medsy.data.remote.network.safeApiCall
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.map
import com.medsy.domain.offer.model.CreateOfferRequest
import com.medsy.domain.offer.model.Offer
import com.medsy.domain.offer.model.PaginatedOffers
import com.medsy.domain.offer.repository.OfferRepository
import javax.inject.Inject

class OfferRepositoryImpl @Inject constructor(
    private val api: OfferApi
) : OfferRepository {

    override suspend fun createOffer(
        requestId: Long,
        request: CreateOfferRequest
    ): MedsyResult<Offer, MedsyError> {
        return safeApiCall { api.createPharmacyOffer(requestId, request.toDto()) }.map { it.toDomain() }
    }

    override suspend fun getOfferById(id: Long): MedsyResult<Offer, MedsyError> {
        return safeApiCall { api.getOfferById(id) }.map { it.toDomain() }
    }

    override suspend fun getPharmacyOffers(
        pharmacyId: Long,
        page: Int,
        size: Int,
        sort: List<String>
    ): MedsyResult<PaginatedOffers, MedsyError> {
        return safeApiCall { api.getPharmacyOffers(pharmacyId, page, size, sort) }.map { it.toDomain() }
    }
}
