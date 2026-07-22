package com.medsy.data.orders.repository

import com.medsy.data.orders.datasource.OrdersRemoteDataSource
import com.medsy.domain.orders.model.OrderDetailsDomain
import com.medsy.domain.orders.model.OrderPageDomain
import com.medsy.domain.orders.repository.OrdersRepository
import toDomain
import javax.inject.Inject

class OrdersRepositoryImpl @Inject constructor(
    private val remoteDataSource: OrdersRemoteDataSource
) : OrdersRepository {

    override suspend fun getCurrentPharmacyRequests(
        page: Int,
        size: Int,
        sort: List<String>?
    ): Result<OrderPageDomain> {
        return runCatching {
            val response = remoteDataSource.getCurrentPharmacyRequests(page, size, sort)
            response.toDomain()
        }
    }

    override suspend fun getOrderDetails(orderId: Long): Result<OrderDetailsDomain?> {
        return runCatching {
            val response = remoteDataSource.getCurrentPharmacyRequests(page = 0, size = 50, sort = null)
            val matchedDto = response.content.find { it.id == orderId }
            matchedDto?.toDomain()
        }
    }
}

