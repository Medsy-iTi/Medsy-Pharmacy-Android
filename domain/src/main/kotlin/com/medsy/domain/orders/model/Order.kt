package com.medsy.domain.orders.model

data class OrderDetailsDomain(
    val id: Long,
    val customerId: Long,
    val deliveryLatitude: Double?,
    val deliveryLongitude: Double?,
    val deliveryAddress: String?,
    val status: String,
    val createdAt: String,
    val items: List<OrderItemDomain>
)

data class OrderItemDomain(
    val id: Long,
    val productId: Long,
    val quantity: Int
)

data class OrderPageDomain(
    val content: List<OrderDetailsDomain>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalElements: Long,
    val totalPages: Int,
    val last: Boolean
)