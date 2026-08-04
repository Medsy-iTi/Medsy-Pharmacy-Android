package com.medsy.domain.offer.repository

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.offer.model.CreateOfferRequest
import com.medsy.domain.offer.model.Offer
import com.medsy.domain.offer.model.PaginatedOffers

interface OfferRepository {
    suspend fun createOffer(requestId: Long, request: CreateOfferRequest): MedsyResult<Offer, MedsyError>
    suspend fun getOfferById(id: Long): MedsyResult<Offer, MedsyError>
    suspend fun getPharmacyOffers(pharmacyId: Long, page: Int, size: Int, sort: List<String>): MedsyResult<PaginatedOffers, MedsyError>
}
