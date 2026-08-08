package com.medsy.data.orders.model

import com.medsy.data.common.remote.dto.ProductSummaryDto
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PharmacyOrderPageDto(
    val content: List<PharmacyOrderDto>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalElements: Long,
    val totalPages: Int,
    val last: Boolean,
)

@JsonClass(generateAdapter = true)
data class PharmacyOrderDto(
    val id: Long,
    val customerId: Long,
    val customerName: String?,
    val customerNotes: String?,
    val deliveryAddress: String?,
    val phoneNumber: String?,
    val prescriptionUrl: String?,
    val pharmacyId: Long?,
    val pharmacyName: String?,
    val pharmacyAddress: String?,
    val pharmacyPhone: String?,
    val pharmacistId: Long?,
    val pharmacistName: String?,
    val offerId: Long?,
    val subTotal: Double?,
    val deliveryFee: Double?,
    val total: Double?,
    val deliveryLatitude: Double?,
    val deliveryLongitude: Double?,
    val createdAt: String?,
    val status: String,
    val paymentMethod: String?,
    val paymentStatus: String?,
    val paidAt: String?,
    val items: List<PharmacyOrderItemDto>,
)

@JsonClass(generateAdapter = true)
data class PharmacyOrderItemDto(
    val id: Long,
    val productId: Long?,
    val quantity: Long,
    val unitPrice: Double?,
    val product: ProductSummaryDto?,
    val totalPrice: Double?,
)
