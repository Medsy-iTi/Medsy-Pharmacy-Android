package com.medsy.data.orders.datasource

import com.medsy.data.orders.model.PharmacyRequestPageDto
import com.medsy.data.orders.remote.dto.CreateOfferRequestDto
import com.medsy.data.remote.api.ApiService
import com.medsy.data.remote.network.safeApiCall
import com.medsy.data.remote.network.safeEmptyRestCall
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import javax.inject.Inject

class RequestsRemoteDataSourceImpl @Inject constructor(
    private val apiService: ApiService
) : RequestsRemoteDataSource {

    override suspend fun getCurrentPharmacyRequests(
        page: Int,
        size: Int,
        sort: List<String>?
    ): MedsyResult<PharmacyRequestPageDto, MedsyError.Remote> =
        safeApiCall { apiService.getCurrentPharmacyRequests(page, size, sort) }

    override suspend fun createOffer(requestId: Long, request: CreateOfferRequestDto): MedsyResult<Unit, MedsyError.Remote> =
        safeEmptyRestCall { apiService.createOffer(requestId, request) }
}