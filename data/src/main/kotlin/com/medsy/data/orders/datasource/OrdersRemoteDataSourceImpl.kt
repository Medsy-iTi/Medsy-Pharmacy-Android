package com.medsy.data.orders.datasource

import com.medsy.data.orders.model.PharmacyOrderDto
import com.medsy.data.orders.model.PharmacyOrderPageDto
import com.medsy.data.orders.model.PharmacyRequestAssignmentDto
import com.medsy.data.orders.model.PharmacyRequestPageDto
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
        sort: List<String>?,
        assignmentStatus: String?,
    ): MedsyResult<PharmacyRequestPageDto, MedsyError.Remote> =
        safeApiCall { apiService.getCurrentPharmacyRequests(page, size, sort, assignmentStatus) }

    override suspend fun getRequestDetails(
        requestId: Long,
    ): MedsyResult<PharmacyRequestAssignmentDto, MedsyError.Remote> =
        safeApiCall { apiService.getRequestById(requestId) }

    override suspend fun getPharmacyOrders(
        pharmacyId: Long,
        page: Int,
        size: Int,
        sort: List<String>?,
    ): MedsyResult<PharmacyOrderPageDto, MedsyError.Remote> =
        safeApiCall { apiService.getPharmacyOrders(pharmacyId, page, size, null) }

    override suspend fun getOrderDetails(
        orderId: Long,
    ): MedsyResult<PharmacyOrderDto, MedsyError.Remote> =
        safeApiCall { apiService.getOrderById(orderId) }
}
