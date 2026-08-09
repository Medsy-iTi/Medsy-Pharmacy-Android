package com.medsy.data.orders.datasource

import com.medsy.data.orders.model.PharmacyOrderDto
import com.medsy.data.orders.model.PharmacyOrderPageDto
import com.medsy.data.orders.model.PharmacyRequestDto
import com.medsy.data.orders.model.PharmacyRequestPageDto
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult

interface OrdersRemoteDataSource {
    suspend fun getCurrentPharmacyRequests(
        page: Int,
        size: Int,
        sort: List<String>?
    ): MedsyResult<PharmacyRequestPageDto, MedsyError.Remote>

    suspend fun getRequestDetails(requestId: Long): MedsyResult<PharmacyRequestDto, MedsyError.Remote>

    suspend fun getPharmacyOrders(
        pharmacyId: Long,
        page: Int,
        size: Int,
        sort: List<String>?,
    ): MedsyResult<PharmacyOrderPageDto, MedsyError.Remote>

    suspend fun getOrderDetails(orderId: Long): MedsyResult<PharmacyOrderDto, MedsyError.Remote>
}
