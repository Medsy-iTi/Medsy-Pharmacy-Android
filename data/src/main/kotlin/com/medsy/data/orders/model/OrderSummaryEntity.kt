package com.medsy.data.orders.model


data class OrderSummaryEntity(
    val id: String,
    val minutesAgo: Int,
    val status: String,
    val customerName: String,
    val customerPhone: String,
    val customerAddress: String,
    val total: Double,
    val paymentMethod: String,
    val paymentCardLastDigits: String? = null,
)