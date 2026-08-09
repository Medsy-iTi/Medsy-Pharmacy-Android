package com.medsy.data.orders.model

import com.medsy.data.common.remote.dto.ProductSummaryDto
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
    val productId: Long?,
    val quantity: Long,
    val unitPrice: Double?,
    val product: ProductSummaryDto?,
)
