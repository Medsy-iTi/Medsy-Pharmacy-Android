package com.medsy.presentation.orderdetails.mapper

import com.medsy.domain.orders.model.OrderDetailsDomain
import com.medsy.domain.orders.model.OrderItemDomain
import com.medsy.presentation.orderdetails.model.Order
import com.medsy.presentation.orderdetails.model.OrderMedicineItem

fun OrderDetailsDomain.toPresentation(): Order {
    return Order(
        id = this.id.toString(),
        isNew = this.status.equals("NEW", ignoreCase = true),
        minutesAgo = 0,
        customerName = "Customer #${this.customerId}",
        customerPhone = "",
        customerAddress = this.deliveryAddress ?: "",
        items = this.items.map { it.toPresentation() },
        customerNotes = null,
        total = 0
    )
}

fun OrderItemDomain.toPresentation(): OrderMedicineItem {
    return OrderMedicineItem(
        id = this.id.toString(),
        name = "Product #${this.productId}",
        packInfo = "",
        quantity = this.quantity,
        price = 0,
        imageUrl = null,
        productId = this.productId
    )
}