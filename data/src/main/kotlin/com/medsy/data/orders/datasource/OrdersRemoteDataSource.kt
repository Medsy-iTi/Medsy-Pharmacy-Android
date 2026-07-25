package com.medsy.data.orders.datasource

import com.medsy.data.orders.model.PharmacyRequestPageDto
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult

interface RequestsRemoteDataSource {
    suspend fun getCurrentPharmacyRequests(
        page: Int,
        size: Int,
        sort: List<String>?
    ): MedsyResult<PharmacyRequestPageDto, MedsyError.Remote>
}