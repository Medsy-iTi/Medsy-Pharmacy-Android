package com.medsy.presentation.orderdetails.model

import com.medsy.domain.orders.model.OrderDetails
import com.medsy.domain.orders.model.OrderItem

data class OrderMedicineItem(
    val id: String,
    val name: String,
    val packInfo: String,
    val quantity: Int,
    val price: Int,
    val imageUrl: String?,
)

data class Order(
    val id: String,
    val isNew: Boolean,
    val minutesAgo: Int,
    val customerName: String,
    val customerPhone: String,
    val customerAddress: String,
    val items: List<OrderMedicineItem>,
    val customerNotes: String?,
    val total: Int,
    val prescriptionUrl: String? = "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?q=80&w=1000&auto=format&fit=crop"
)


fun OrderDetails.toPresentation(): Order {
    return Order(
        id = id,
        isNew = status.name == "NEW",
        minutesAgo = minutesAgo,
        customerName = customerName,
        customerPhone = customerPhone,
        customerAddress = customerAddress,
        items = items.map { it.toPresentation() },
        customerNotes = customerNotes,
        total = total.toInt()
    )
}

fun OrderItem.toPresentation(): OrderMedicineItem {
    return OrderMedicineItem(
        id = id,
        name = name,
        packInfo = "",
        quantity = quantity,
        price = price.toInt(),
        imageUrl = imageUrl
    )
}
