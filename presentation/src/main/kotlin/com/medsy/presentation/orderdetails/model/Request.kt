package com.medsy.presentation.orderdetails.model


data class RequestMedicineItem(
    val id: String,
    val name: String,
    val packInfo: String,
    val quantity: Int,
    val price: Double,
    val imageUrl: String?,
    val productId: Long? = null,
)

data class Request(
    val id: String,
    val isNew: Boolean,
    val minutesAgo: Int,
    val customerName: String,
    val customerPhone: String,
    val customerAddress: String,
    val deliveryLatitude: Double?,
    val deliveryLongitude: Double?,
    val items: List<RequestMedicineItem>,
    val customerNotes: String?,
    val total: Double,
    val prescriptionUrl: String?,
    val paymentMethod: String
)
