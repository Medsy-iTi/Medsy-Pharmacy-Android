package com.medsy.data.orders.repository

import com.medsy.data.orders.datasource.OrdersRemoteDataSource
import com.medsy.data.orders.mapper.toDomain
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.map
import com.medsy.domain.orders.model.PharmacyOrderDomain
import com.medsy.domain.orders.model.PharmacyOrderPageDomain
import com.medsy.domain.orders.model.PharmacyRequestDomain
import com.medsy.domain.orders.model.PharmacyRequestPageDomain
import com.medsy.domain.orders.repository.OrdersRepository
import javax.inject.Inject

class OrdersRepositoryImpl @Inject constructor(
    private val remoteDataSource: OrdersRemoteDataSource,
) : OrdersRepository {

    private val requestCache = mutableMapOf<Long, PharmacyRequestDomain>()
    private val orderCache = mutableMapOf<Long, PharmacyOrderDomain>()

    override suspend fun getCurrentPharmacyRequests(
        page: Int,
        size: Int,
        sort: List<String>?
    ): MedsyResult<PharmacyRequestPageDomain, MedsyError.Remote> =
        remoteDataSource.getCurrentPharmacyRequests(page, size, sort).map { dto ->
            dto.toDomain().also { pageData ->
                pageData.content.forEach { requestCache[it.id] = it }
            }
        }

    override suspend fun getRequestDetails(
        requestId: Long,
    ): MedsyResult<PharmacyRequestDomain, MedsyError.Remote> =
        remoteDataSource.getRequestDetails(requestId).map { dto ->
            dto.toDomain().also { requestCache[it.id] = it }
        }

    override suspend fun getPharmacyOrders(
        pharmacyId: Long,
        page: Int,
        size: Int,
        sort: List<String>?,
    ): MedsyResult<PharmacyOrderPageDomain, MedsyError.Remote> =
        remoteDataSource.getPharmacyOrders(pharmacyId, page, size, sort).map { dto ->
            dto.toDomain().also { pageData ->
                pageData.content.forEach { orderCache[it.id] = it }
            }
        }

    override suspend fun getOrderDetails(
        orderId: Long,
    ): MedsyResult<PharmacyOrderDomain, MedsyError.Remote> =
        remoteDataSource.getOrderDetails(orderId).map { dto ->
            dto.toDomain().also { orderCache[it.id] = it }
        }

    override fun getCachedRequest(requestId: Long): PharmacyRequestDomain? = requestCache[requestId]

    override fun getCachedOrder(orderId: Long): PharmacyOrderDomain? = orderCache[orderId]
}
