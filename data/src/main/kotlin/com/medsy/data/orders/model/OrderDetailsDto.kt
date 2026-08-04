package com.medsy.data.orders.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PharmacyRequestPageDto(
    val content: List<PharmacyRequestDto>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalElements: Long,
    val totalPages: Int,
    val last: Boolean
)

@JsonClass(generateAdapter = true)
data class PharmacyRequestDto(
    val id: Long,
    val offerId: Long? = null,
    val orderId: Long? = null,
    val customerId: Long,
    val deliveryLatitude: Double?,
    val deliveryLongitude: Double?,
    val deliveryAddress: String?,
    val status: String,
    val createdAt: String,
    val items: List<RequestItemDto>,
    val prescriptionUrl: String?,
    val customerName: String?,
    val customerPhone: String?,
    val paymentMethod: String?,
    val notes: String?
)

@JsonClass(generateAdapter = true)
data class RequestItemDto(
    val id: Long,
    val productId: Long,
    val imageUrl: String?,
    val productName: String,
    val strength: String?,
    val packSize: String?,
    val form: String?,
    val quantity: Int,
    val unitPrice: Double
)