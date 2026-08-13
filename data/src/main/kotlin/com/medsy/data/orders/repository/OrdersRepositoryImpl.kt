package com.medsy.data.orders.repository

import com.medsy.data.orders.datasource.OrdersRemoteDataSource
import com.medsy.data.orders.mapper.toDomain
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.map
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.orders.model.PharmacyOrder
import com.medsy.domain.orders.model.PharmacyOrderPage
import com.medsy.domain.orders.model.PharmacyRequest
import com.medsy.domain.orders.model.PharmacyRequestPage
import com.medsy.domain.orders.model.PharmacyRequestAssignmentStatus
import com.medsy.domain.orders.repository.OrdersRepository
import javax.inject.Inject

class OrdersRepositoryImpl @Inject constructor(
    private val remoteDataSource: OrdersRemoteDataSource,
) : OrdersRepository {

    private val requestCache = mutableMapOf<Long, PharmacyRequest>()
    private val orderCache = mutableMapOf<Long, PharmacyOrder>()

    override suspend fun getCurrentPharmacyRequests(
        page: Int,
        size: Int,
        sort: List<String>?,
        assignmentStatus: PharmacyRequestAssignmentStatus?,
    ): MedsyResult<PharmacyRequestPage, MedsyError.Remote> =
        remoteDataSource.getCurrentPharmacyRequests(
            page,
            size,
            sort,
            assignmentStatus?.apiValue,
        ).map { dto ->
            dto.toDomain().also { pageData ->
                pageData.content.forEach { requestCache[it.id] = it }
            }
        }

    override suspend fun getRequestDetails(
        requestId: Long,
    ): MedsyResult<PharmacyRequest, MedsyError.Remote> =
        remoteDataSource.getRequestDetails(requestId).map { dto ->
            dto.toDomain().also { requestCache[it.id] = it }
        }

    override suspend fun getPharmacyOrders(
        pharmacyId: Long,
        page: Int,
        size: Int,
        sort: List<String>?,
    ): MedsyResult<PharmacyOrderPage, MedsyError.Remote> =
        remoteDataSource.getPharmacyOrders(pharmacyId, page, size, sort).map { dto ->
            dto.toDomain().also { pageData ->
                pageData.content.forEach { orderCache[it.id] = it }
            }
        }

    override suspend fun getOrderDetails(
        orderId: Long,
    ): MedsyResult<PharmacyOrder, MedsyError.Remote> =
        remoteDataSource.getOrderDetails(orderId).map { dto ->
            dto.toDomain().also { orderCache[it.id] = it }
        }

    override suspend fun markOrderReady(
        orderId: Long,
    ): EmptyMedsyResult<MedsyError.Remote> = remoteDataSource.markOrderReady(orderId)

    override fun getCachedRequest(requestId: Long): PharmacyRequest? = requestCache[requestId]

    override fun getCachedOrder(orderId: Long): PharmacyOrder? = orderCache[orderId]
}
