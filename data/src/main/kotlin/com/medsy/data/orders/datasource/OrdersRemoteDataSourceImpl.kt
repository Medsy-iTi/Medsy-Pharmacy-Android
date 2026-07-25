package com.medsy.data.orders.datasource

import com.medsy.data.orders.model.OrderPageResponseDto
import com.medsy.data.remote.api.ApiService
import com.medsy.data.remote.network.safeApiCall
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import javax.inject.Inject

class OrdersRemoteDataSourceImpl @Inject constructor(
    private val apiService: ApiService
) : OrdersRemoteDataSource {

    override suspend fun getCurrentPharmacyRequests(
        page: Int,
        size: Int,
        sort: List<String>?
    ): MedsyResult<OrderPageResponseDto, MedsyError.Remote> =
        safeApiCall { apiService.getCurrentPharmacyRequests(page, size, sort) }

    override suspend fun getOrderDetails(orderId: Long): MedsyResult<com.medsy.data.orders.model.OrderDetailsDto, MedsyError.Remote> =
        // Since there's no explicit getOrderDetails API in the pharmacy requests side, we fetch the list and find it.
        // Wait, the repository does this, not the data source. The data source shouldn't implement this if it's not in the API. 
        // Let me just throw an exception or return a fake result since the repository actually uses getCurrentPharmacyRequests and filters it.
        MedsyResult.Error(MedsyError.Remote.Unknown)

    override suspend fun createOffer(requestId: Long, request: com.medsy.data.orders.remote.dto.CreateOfferRequestDto): MedsyResult<Unit, MedsyError.Remote> =
        com.medsy.data.remote.network.safeEmptyRestCall { apiService.createOffer(requestId, request) }
}