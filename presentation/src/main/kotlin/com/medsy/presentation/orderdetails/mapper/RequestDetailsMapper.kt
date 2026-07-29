package com.medsy.presentation.orderdetails.mapper

import com.medsy.domain.orders.model.PharmacyRequestDomain
import com.medsy.domain.orders.model.RequestItemDomain
import com.medsy.domain.orders.model.RequestStatusConstants
import com.medsy.presentation.orderdetails.model.Request
import com.medsy.presentation.orderdetails.model.RequestMedicineItem
import com.medsy.presentation.orderdetails.model.PaymentMethod
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
        id = (this.orderId?.toString() ?: this.offerId?.toString() ?: this.id.toString()),
        isNew = (this.status.equals(RequestStatusConstants.SEARCHING, ignoreCase = true) ||
                this.status.equals(RequestStatusConstants.NEW, ignoreCase = true)) && minutes < 60,
        minutesAgo = minutes,
        customerName = this.customerName,
        customerId = this.customerId,
        customerPhone = this.customerPhone ?: "",
        customerAddress = this.deliveryAddress ?: "",
        deliveryLatitude = this.deliveryLatitude,
        deliveryLongitude = this.deliveryLongitude,
        items = this.items.map { it.toPresentation() },
        customerNotes = this.notes,
        total = calculatedTotal,
        prescriptionUrl = this.prescriptionUrl,
        paymentMethod = PaymentMethod.fromApiValue(this.paymentMethod)
    )
}

fun RequestItemDomain.toPresentation(): RequestMedicineItem {
    val localStrength = strength
    val localPackSize = packSize
    val localForm = form

    val parts = mutableListOf<String>()
    if (!localStrength.isNullOrBlank()) parts.add(localStrength)
    if (!localPackSize.isNullOrBlank()) {
        if (!localForm.isNullOrBlank()) {
            parts.add("$localPackSize $localForm")
        } else {
            parts.add(localPackSize)
        }
    } else if (!localForm.isNullOrBlank()) {
        parts.add(localForm)
    }
    val info = parts.joinToString(" • ")

    return RequestMedicineItem(
        id = this.id.toString(),
        name = this.productName,
        packInfo = info,
        quantity = this.quantity,
        price = this.unitPrice,
        imageUrl = this.imageUrl,
        productId = this.productId
    )
}