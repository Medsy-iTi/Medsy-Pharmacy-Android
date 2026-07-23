package com.medsy.data.orders.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OrderPageResponseDto(
    val content: List<OrderDetailsDto>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalElements: Long,
    val totalPages: Int,
    val last: Boolean
)

@JsonClass(generateAdapter = true)
data class OrderDetailsDto(
    val id: Long,
    val customerId: Long,
    val deliveryLatitude: Double?,
    val deliveryLongitude: Double?,
    val deliveryAddress: String?,
    val status: String,
    val createdAt: String,
    val items: List<OrderItemDto>
)

@JsonClass(generateAdapter = true)
data class OrderItemDto(
    val id: Long,
    val productId: Long,
    val quantity: Int
)