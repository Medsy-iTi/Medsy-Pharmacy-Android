package com.medsy.presentation.orderdetails.mapper

import com.medsy.domain.orders.model.PharmacyRequestDomain
import com.medsy.domain.orders.model.RequestItemDomain
import com.medsy.presentation.orderdetails.model.Request
import com.medsy.presentation.orderdetails.model.RequestMedicineItem
import java.time.Duration
import java.time.Instant

fun calculateMinutesAgo(createdAt: String): Int {
    return try {
        val parseStr = if (createdAt.endsWith("Z")) createdAt else "${createdAt}Z"
        val created = Instant.parse(parseStr)
        val now = Instant.now()
        Duration.between(created, now).toMinutes().coerceAtLeast(0).toInt()
    } catch (e: Exception) {
        0
    }
}

fun PharmacyRequestDomain.toPresentation(): Request {
    val calculatedTotal = items.sumOf { it.unitPrice * it.quantity }
    val minutes = calculateMinutesAgo(createdAt)
    return Request(
        id = this.id.toString(),
        isNew = this.status.equals("SEARCHING", ignoreCase = true) ||
                this.status.equals("NEW", ignoreCase = true),
        minutesAgo = minutes,
        customerName = this.customerName ?: "Customer #${this.customerId}",
        customerPhone = this.customerPhone ?: "",
        customerAddress = this.deliveryAddress ?: "",
        items = this.items.map { it.toPresentation() },
        customerNotes = null,
        total = calculatedTotal,
        prescriptionUrl = this.prescriptionUrl,
        paymentMethod = when (this.paymentMethod?.uppercase()) {
            "VISA" -> "Visa"
            "MASTERCARD" -> "Mastercard"
            else -> "Cash"
        }
    )
}

fun RequestItemDomain.toPresentation(): RequestMedicineItem {
    return RequestMedicineItem(
        id = this.id.toString(),
        name = this.productName,
        packInfo = "",
        quantity = this.quantity,
        price = this.unitPrice,
        imageUrl = this.imageUrl
    )
}