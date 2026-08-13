package com.medsy.data.orders.mapper

import com.medsy.data.BuildConfig.BASE_URL
import com.medsy.data.orders.model.PharmacyRequestAssignmentDto
import com.medsy.data.orders.model.PharmacyRequestPageDto
import com.medsy.data.orders.model.PharmacyOrderDto
import com.medsy.data.orders.model.PharmacyOrderItemDto
import com.medsy.data.orders.model.PharmacyOrderPageDto
import com.medsy.data.orders.model.RequestItemDto
import com.medsy.domain.orders.model.OrderItem
import com.medsy.domain.orders.model.PaymentMethod
import com.medsy.domain.orders.model.PharmacyOrder
import com.medsy.domain.orders.model.PharmacyOrderPage
import com.medsy.domain.orders.model.PharmacyOrderStatus
import com.medsy.domain.orders.model.PharmacyRequest
import com.medsy.domain.orders.model.PharmacyRequestAssignmentStatus
import com.medsy.domain.orders.model.PharmacyRequestPage
import com.medsy.domain.orders.model.RequestItem

fun PharmacyRequestPageDto.toDomain(): PharmacyRequestPage {
    return PharmacyRequestPage(
        content = content.map { it.toDomain() },
        pageNumber = pageNumber,
        pageSize = pageSize,
        totalElements = totalElements,
        totalPages = totalPages,
        last = last
    )
}

fun PharmacyRequestAssignmentDto.toDomain(): PharmacyRequest {
    val medicineRequest = request
    return PharmacyRequest(
        id = medicineRequest.id,
        customerId = medicineRequest.customerId,
        deliveryLatitude = medicineRequest.deliveryLatitude,
        deliveryLongitude = medicineRequest.deliveryLongitude,
        deliveryAddress = medicineRequest.deliveryAddress,
        requestStatus = medicineRequest.status,
        assignmentStatus = PharmacyRequestAssignmentStatus.fromApiValue(assignmentStatus),
        distanceKm = distanceKm,
        createdAt = medicineRequest.createdAt,
        items = medicineRequest.items.map { it.toDomain() },
        prescriptionUrl = medicineRequest.prescriptionUrl?.let { if (it.startsWith("http")) it else "${BASE_URL}$it" },
        customerName = medicineRequest.customerName,
        customerPhone = medicineRequest.customerPhone,
        paymentMethod = PaymentMethod.fromApiValue(medicineRequest.paymentMethod),
        notes = medicineRequest.notes
    )
}

fun RequestItemDto.toDomain(): RequestItem {
    val resolvedProduct = product
    return RequestItem(
        id = id,
        productId = productId ?: resolvedProduct?.id ?: 0L,
        imageUrl = resolvedProduct?.imageUrl,
        productName = resolvedProduct?.productName ?: resolvedProduct?.name.orEmpty(),
        strength = resolvedProduct?.strength,
        packSize = resolvedProduct?.packSize,
        form = resolvedProduct?.form,
        quantity = quantity.coerceIn(0, Int.MAX_VALUE.toLong()).toInt(),
        unitPrice = unitPrice ?: resolvedProduct?.price ?: 0.0,
    )
}

fun PharmacyOrderPageDto.toDomain(): PharmacyOrderPage = PharmacyOrderPage(
    content = content.map(PharmacyOrderDto::toDomain),
    pageNumber = pageNumber,
    pageSize = pageSize,
    totalElements = totalElements,
    totalPages = totalPages,
    last = last,
)

fun PharmacyOrderDto.toDomain(): PharmacyOrder = PharmacyOrder(
    id = id,
    customerId = customerId,
    customerName = customerName,
    customerNotes = customerNotes,
    customerPhone = phoneNumber,
    deliveryAddress = deliveryAddress,
    deliveryLatitude = deliveryLatitude,
    deliveryLongitude = deliveryLongitude,
    prescriptionUrl = prescriptionUrl?.let { if (it.startsWith("http")) it else "${BASE_URL}$it" },
    offerId = offerId,
    subTotal = subTotal ?: 0.0,
    total = total ?: 0.0,
    createdAt = createdAt,
    status = PharmacyOrderStatus.fromApiValue(status),
    paymentMethod = PaymentMethod.fromApiValue(paymentMethod),
    items = items.map(PharmacyOrderItemDto::toDomain),
)

fun PharmacyOrderItemDto.toDomain(): OrderItem = OrderItem(
    id = id,
    productId = productId ?: product?.id,
    productName = product?.productName ?: product?.name.orEmpty(),
    strength = product?.strength,
    packSize = product?.packSize,
    form = product?.form,
    quantity = quantity.coerceIn(0, Int.MAX_VALUE.toLong()).toInt(),
    unitPrice = unitPrice ?: product?.price ?: 0.0,
    imageUrl = product?.imageUrl,
)
