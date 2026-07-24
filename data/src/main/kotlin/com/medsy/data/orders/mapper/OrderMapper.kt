package com.medsy.data.orders.mapper

import com.medsy.data.orders.model.PharmacyRequestDto
import com.medsy.data.orders.model.PharmacyRequestPageDto
import com.medsy.data.orders.model.RequestItemDto
import com.medsy.domain.orders.model.PharmacyRequestDomain
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

fun PharmacyRequestDto.toDomain(): PharmacyRequestDomain {
    return PharmacyRequestDomain(
        id = id,
        customerId = customerId,
        deliveryLatitude = deliveryLatitude,
        deliveryLongitude = deliveryLongitude,
        deliveryAddress = deliveryAddress,
        status = status,
        createdAt = createdAt,
        items = items.map { it.toDomain() },
        prescriptionUrl = prescriptionUrl
    )
}

fun RequestItemDto.toDomain(): RequestItemDomain {
    return RequestItemDomain(
        id = id,
        productId = productId,
        imageUrl = imageUrl,
        productName = productName,
        quantity = quantity,
        unitPrice = unitPrice
    )
}