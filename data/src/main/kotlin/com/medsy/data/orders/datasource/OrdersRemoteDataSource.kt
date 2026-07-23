package com.medsy.data.orders.datasource

import com.medsy.data.orders.model.OrderPageResponseDto
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult

interface OrdersRemoteDataSource {
    suspend fun getCurrentPharmacyRequests(
        page: Int,
        size: Int,
        sort: List<String>?
    ): MedsyResult<OrderPageResponseDto, MedsyError.Remote>
}