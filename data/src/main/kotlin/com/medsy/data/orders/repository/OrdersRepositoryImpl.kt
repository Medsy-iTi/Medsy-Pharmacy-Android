package com.medsy.data.orders.repository

import com.medsy.data.orders.datasource.RequestsRemoteDataSource
import com.medsy.data.orders.mapper.toDomain
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.map
import com.medsy.domain.orders.model.PharmacyRequestDomain
import com.medsy.domain.orders.model.PharmacyRequestPageDomain
import com.medsy.domain.orders.repository.RequestsRepository
import javax.inject.Inject

class RequestsRepositoryImpl @Inject constructor(
    private val remoteDataSource: RequestsRemoteDataSource
) : RequestsRepository {

    override suspend fun getCurrentPharmacyRequests(
        page: Int,
        size: Int,
        sort: List<String>?
    ): MedsyResult<PharmacyRequestPageDomain, MedsyError.Remote> =
        remoteDataSource.getCurrentPharmacyRequests(page, size, sort)
            .map { it.toDomain() }

    override suspend fun getRequestDetails(requestId: Long): MedsyResult<PharmacyRequestDomain?, MedsyError.Remote> =
        remoteDataSource.getCurrentPharmacyRequests(page = 0, size = 50, sort = null)
            .map { page -> page.content.find { it.id == requestId }?.toDomain() }
}
