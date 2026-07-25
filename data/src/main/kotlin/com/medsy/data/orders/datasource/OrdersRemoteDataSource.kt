package com.medsy.data.orders.datasource

import com.medsy.data.orders.model.OrderPageResponseDto
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.data.orders.model.OrderDetailsDto

interface OrdersRemoteDataSource {
    suspend fun getCurrentPharmacyRequests(
        page: Int,
        size: Int,
        sort: List<String>?
    ): MedsyResult<OrderPageResponseDto, MedsyError.Remote>

    suspend fun getOrderDetails(orderId: Long): MedsyResult<OrderDetailsDto, MedsyError.Remote>

    suspend fun createOffer(requestId: Long, request: com.medsy.data.orders.remote.dto.CreateOfferRequestDto): MedsyResult<Unit, MedsyError.Remote>
}