package com.medsy.data.orders.model

import com.medsy.domain.orders.model.OrderDetails
import com.medsy.domain.orders.model.OrderItem
import com.medsy.domain.orders.model.OrderStatus
import com.medsy.domain.orders.model.PaymentMethod

data class OrderDetailsResponse(
    val id: String,
    val minutesAgo: Int,
    val status: String,
    val customerName: String,
    val customerPhone: String,
    val customerAddress: String,
    val total: Double,
    val paymentMethod: String,
    val paymentCardLastDigits: String?,
    val items: List<OrderItemResponse>
)

data class OrderItemResponse(
    val id: String,
    val name: String,
    val quantity: Int,
    val price: Double,
    val imageUrl: String?
)

fun OrderDetailsResponse.toDomain(): OrderDetails {
    return OrderDetails(
        id = id,
        minutesAgo = minutesAgo,
        status = when (status.lowercase()) {
            "new" -> OrderStatus.New
            "inprogress", "in_progress" -> OrderStatus.InProgress
            "delivered" -> OrderStatus.Delivered
            else -> OrderStatus.New
        },
        customerName = customerName,
        customerPhone = customerPhone,
        customerAddress = customerAddress,
        total = total,
        paymentMethod = when (paymentMethod.lowercase()) {
            "cash" -> PaymentMethod.Cash
            "visa" -> PaymentMethod.Visa
            else -> PaymentMethod.Cash
        },
        paymentCardLastDigits = paymentCardLastDigits,
        items = items.map { it.toDomain() }
    )
}

fun OrderItemResponse.toDomain(): OrderItem {
    return OrderItem(
        id = id,
        name = name,
        quantity = quantity,
        price = price,
        imageUrl = imageUrl
    )
}