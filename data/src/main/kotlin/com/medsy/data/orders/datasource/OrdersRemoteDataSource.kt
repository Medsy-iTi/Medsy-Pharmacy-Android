package com.medsy.data.orders.datasource

import com.medsy.data.orders.model.PharmacyOrderDto
import com.medsy.data.orders.model.PharmacyOrderPageDto
import com.medsy.data.orders.model.PharmacyRequestAssignmentDto
import com.medsy.data.orders.model.PharmacyRequestPageDto
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.EmptyMedsyResult

interface OrdersRemoteDataSource {
    suspend fun getCurrentPharmacyRequests(
        page: Int,
        size: Int,
        sort: List<String>?,
        assignmentStatus: String? = null,
    ): MedsyResult<PharmacyRequestPageDto, MedsyError.Remote>

    suspend fun getRequestDetails(requestId: Long): MedsyResult<PharmacyRequestAssignmentDto, MedsyError.Remote>

    suspend fun getPharmacyOrders(
        pharmacyId: Long,
        page: Int,
        size: Int,
        sort: List<String>?,
        status: String? = null,
    ): MedsyResult<PharmacyOrderPageDto, MedsyError.Remote>

    suspend fun getOrderDetails(orderId: Long): MedsyResult<PharmacyOrderDto, MedsyError.Remote>

    suspend fun markOrderReady(orderId: Long): EmptyMedsyResult<MedsyError.Remote>

    suspend fun markOrderOutForDelivery(orderId: Long): EmptyMedsyResult<MedsyError.Remote>

    suspend fun markOrderDelivered(orderId: Long): EmptyMedsyResult<MedsyError.Remote>
}
