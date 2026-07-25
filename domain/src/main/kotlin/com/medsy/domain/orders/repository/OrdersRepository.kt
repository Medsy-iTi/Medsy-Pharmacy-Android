package com.medsy.domain.orders.repository

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.orders.model.PharmacyRequestDomain
import com.medsy.domain.orders.model.PharmacyRequestPageDomain


interface RequestsRepository {

    suspend fun getCurrentPharmacyRequests(
        page: Int,
        size: Int,
        sort: List<String>?
    ): MedsyResult<PharmacyRequestPageDomain, MedsyError.Remote>

    suspend fun getRequestDetails(requestId: Long): MedsyResult<PharmacyRequestDomain?, MedsyError.Remote>
}