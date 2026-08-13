package com.medsy.data.orders.model

import com.medsy.data.common.remote.dto.ProductSummaryDto
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PharmacyOrderPageDto(
    val content: List<PharmacyOrderDto> = emptyList(),
    val pageNumber: Int = 0,
    val pageSize: Int = 0,
    val totalElements: Long = 0,
    val totalPages: Int = 0,
    val last: Boolean = true,
)

@JsonClass(generateAdapter = true)
data class PharmacyOrderDto(
    val id: Long,
    val customerId: Long,
    val customerName: String? = null,
    val customerNotes: String? = null,
    val deliveryAddress: String? = null,
    val phoneNumber: String? = null,
    val prescriptionUrl: String? = null,
    val pharmacyId: Long? = null,
    val pharmacyName: String? = null,
    val pharmacyAddress: String? = null,
    val pharmacyPhone: String? = null,
    val pharmacistId: Long? = null,
    val pharmacistName: String? = null,
    val offerId: Long? = null,
    val subTotal: Double? = null,
    val total: Double? = null,
    val deliveryLatitude: Double? = null,
    val deliveryLongitude: Double? = null,
    val createdAt: String? = null,
    val status: String? = null,
    val paymentMethod: String? = null,
    val items: List<PharmacyOrderItemDto> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class PharmacyOrderItemDto(
    val id: Long,
    val productId: Long? = null,
    val quantity: Long = 0,
    val unitPrice: Double? = null,
    val product: ProductSummaryDto? = null,
    val totalPrice: Double? = null,
)
