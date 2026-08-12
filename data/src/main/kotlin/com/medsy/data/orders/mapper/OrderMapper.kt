package com.medsy.data.orders.mapper

import com.medsy.data.BuildConfig.BASE_URL
import com.medsy.data.orders.model.PharmacyRequestAssignmentDto
import com.medsy.data.orders.model.PharmacyRequestPageDto
import com.medsy.data.orders.model.PharmacyOrderDto
import com.medsy.data.orders.model.PharmacyOrderItemDto
import com.medsy.data.orders.model.PharmacyOrderPageDto
import com.medsy.data.orders.model.RequestItemDto
import com.medsy.domain.orders.model.OrderItemDomain
import com.medsy.domain.orders.model.PharmacyOrderDomain
import com.medsy.domain.orders.model.PharmacyOrderPageDomain
import com.medsy.domain.orders.model.PharmacyRequestDomain
import com.medsy.domain.orders.model.PharmacyRequestAssignmentStatus
import com.medsy.domain.orders.model.PharmacyRequestPageDomain
import com.medsy.domain.orders.model.RequestItemDomain

fun PharmacyRequestPageDto.toDomain(): PharmacyRequestPageDomain {
    return PharmacyRequestPageDomain(
        content = content.map { it.toDomain() },
        pageNumber = pageNumber,
        pageSize = pageSize,
        totalElements = totalElements,
        totalPages = totalPages,
        last = last
    )
}

fun PharmacyRequestAssignmentDto.toDomain(): PharmacyRequestDomain {
    val medicineRequest = request
    return PharmacyRequestDomain(
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
        paymentMethod = medicineRequest.paymentMethod,
        notes = medicineRequest.notes
    )
}

fun RequestItemDto.toDomain(): RequestItemDomain {
    val resolvedProduct = product
    return RequestItemDomain(
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

fun PharmacyOrderPageDto.toDomain(): PharmacyOrderPageDomain = PharmacyOrderPageDomain(
    content = content.map(PharmacyOrderDto::toDomain),
    pageNumber = pageNumber,
    pageSize = pageSize,
    totalElements = totalElements,
    totalPages = totalPages,
    last = last,
)

fun PharmacyOrderDto.toDomain(): PharmacyOrderDomain = PharmacyOrderDomain(
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
    deliveryFee = deliveryFee ?: 0.0,
    total = total ?: 0.0,
    createdAt = createdAt,
    status = status,
    paymentMethod = paymentMethod,
    paymentStatus = paymentStatus,
    paidAt = paidAt,
    items = items.map(PharmacyOrderItemDto::toDomain),
)

fun PharmacyOrderItemDto.toDomain(): OrderItemDomain = OrderItemDomain(
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
