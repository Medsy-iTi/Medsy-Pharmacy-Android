package com.medsy.domain.orders.repository

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.orders.model.PharmacyRequestDomain
import com.medsy.domain.orders.model.PharmacyRequestPageDomain
import com.medsy.domain.orders.model.PharmacyOrderDomain
import com.medsy.domain.orders.model.PharmacyOrderPageDomain

interface OrdersRepository {

    suspend fun getCurrentPharmacyRequests(
        page: Int,
        size: Int,
        sort: List<String>?
    ): MedsyResult<PharmacyRequestPageDomain, MedsyError.Remote>

    suspend fun getRequestDetails(requestId: Long): MedsyResult<PharmacyRequestDomain, MedsyError.Remote>

    suspend fun getPharmacyOrders(
        pharmacyId: Long,
        page: Int,
        size: Int,
        sort: List<String>?,
    ): MedsyResult<PharmacyOrderPageDomain, MedsyError.Remote>

    suspend fun getOrderDetails(orderId: Long): MedsyResult<PharmacyOrderDomain, MedsyError.Remote>

    fun getCachedRequest(requestId: Long): PharmacyRequestDomain?

    fun getCachedOrder(orderId: Long): PharmacyOrderDomain?
}
