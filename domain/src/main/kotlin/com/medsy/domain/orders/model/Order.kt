package com.medsy.domain.orders.model

data class PharmacyRequestDomain(
    val id: Long,
    val customerId: Long,
    val deliveryLatitude: Double?,
    val deliveryLongitude: Double?,
    val deliveryAddress: String?,
    val status: String,
    val createdAt: String,
    val items: List<RequestItemDomain>,
    val prescriptionUrl: String?
)

data class RequestItemDomain(
    val id: Long,
    val productId: Long,
    val imageUrl: String?,
    val productName: String,
    val quantity: Int,
    val unitPrice: Double
)

data class PharmacyRequestPageDomain(
    val content: List<PharmacyRequestDomain>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalElements: Long,
    val totalPages: Int,
    val last: Boolean
)