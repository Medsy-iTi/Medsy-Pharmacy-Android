package com.medsy.data.orders.repository

import com.medsy.data.orders.datasource.OrdersRemoteDataSource
import com.medsy.data.orders.mapper.toDomain
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.map
import com.medsy.domain.orders.model.OrderDetailsDomain
import com.medsy.domain.orders.model.OrderPageDomain
import com.medsy.domain.orders.repository.OrdersRepository
import javax.inject.Inject

class OrdersRepositoryImpl @Inject constructor(
    private val remoteDataSource: OrdersRemoteDataSource
) : OrdersRepository {

    override suspend fun getCurrentPharmacyRequests(
        page: Int,
        size: Int,
        sort: List<String>?
    ): MedsyResult<OrderPageDomain, MedsyError.Remote> =
        remoteDataSource.getCurrentPharmacyRequests(page, size, sort)
            .map { it.toDomain() }

    override suspend fun getOrderDetails(orderId: Long): MedsyResult<OrderDetailsDomain?, MedsyError.Remote> =
        remoteDataSource.getCurrentPharmacyRequests(page = 0, size = 50, sort = null)
            .map { page -> page.content.find { it.id == orderId }?.toDomain() }
}
