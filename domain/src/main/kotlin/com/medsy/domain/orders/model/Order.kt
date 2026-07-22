package com.medsy.domain.orders.model

enum class OrderStatus {
    New,
    InProgress,
    Completed,
    Cancelled,
    Delivered
}

enum class PaymentMethod {
    Cash,
    Visa
}

data class OrderSummary(
    val id: String,
    val minutesAgo: Int,
    val status: OrderStatus,
    val customerName: String,
    val customerPhone: String,
    val customerAddress: String,
    val total: Double,
    val paymentMethod: PaymentMethod,
    val paymentCardLastDigits: String? = null,
)