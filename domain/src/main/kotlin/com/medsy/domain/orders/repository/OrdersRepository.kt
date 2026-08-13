package com.medsy.domain.orders.repository

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.orders.model.PharmacyRequest
import com.medsy.domain.orders.model.PharmacyRequestPage
import com.medsy.domain.orders.model.PharmacyRequestAssignmentStatus
import com.medsy.domain.orders.model.PharmacyOrder
import com.medsy.domain.orders.model.PharmacyOrderPage
import com.medsy.domain.orders.model.PharmacyOrderStatus

interface OrdersRepository {

    suspend fun getCurrentPharmacyRequests(
        page: Int,
        size: Int,
        sort: List<String>?,
        assignmentStatus: PharmacyRequestAssignmentStatus? = null,
    ): MedsyResult<PharmacyRequestPage, MedsyError.Remote>

    suspend fun getRequestDetails(requestId: Long): MedsyResult<PharmacyRequest, MedsyError.Remote>

    suspend fun getPharmacyOrders(
        pharmacyId: Long,
        page: Int,
        size: Int,
        sort: List<String>?,
        statuses: List<PharmacyOrderStatus>? = null,
    ): MedsyResult<PharmacyOrderPage, MedsyError.Remote>

    suspend fun getOrderDetails(orderId: Long): MedsyResult<PharmacyOrder, MedsyError.Remote>

    suspend fun markOrderReady(orderId: Long): EmptyMedsyResult<MedsyError.Remote>

    suspend fun markOrderOutForDelivery(orderId: Long): EmptyMedsyResult<MedsyError.Remote>

    suspend fun markOrderDelivered(orderId: Long): EmptyMedsyResult<MedsyError.Remote>

    fun getCachedRequest(requestId: Long): PharmacyRequest?

    fun getCachedOrder(orderId: Long): PharmacyOrder?
}
