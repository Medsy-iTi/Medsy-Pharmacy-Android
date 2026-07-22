package com.medsy.domain.orders.model

data class OrderDetails(
    val id: String,
    val minutesAgo: Int,
    val status: OrderStatus,
    val customerName: String,
    val customerPhone: String,
    val customerAddress: String,
    val total: Double,
    val paymentMethod: PaymentMethod,
    val paymentCardLastDigits: String? = null,
    val items: List<OrderItem> = emptyList(),
    val customerNotes: String? = null
)

data class OrderItem(
    val id: String,
    val name: String,
    val quantity: Int,
    val price: Double,
    val imageUrl: String? = null,
)
