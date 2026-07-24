package com.medsy.presentation.orderdetails.mapper

import com.medsy.domain.orders.model.PharmacyRequestDomain
import com.medsy.domain.orders.model.RequestItemDomain
import com.medsy.presentation.orderdetails.model.Request
import com.medsy.presentation.orderdetails.model.RequestMedicineItem

fun PharmacyRequestDomain.toPresentation(): Request {
    return Request(
        id = this.id.toString(),
        isNew = this.status.equals("SEARCHING", ignoreCase = true) ||
                this.status.equals("NEW", ignoreCase = true),
        minutesAgo = 0,
        customerName = "Customer #${this.customerId}",
        customerPhone = "",
        customerAddress = this.deliveryAddress ?: "",
        items = this.items.map { it.toPresentation() },
        customerNotes = null,
        total = 0,
        prescriptionUrl = this.prescriptionUrl
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