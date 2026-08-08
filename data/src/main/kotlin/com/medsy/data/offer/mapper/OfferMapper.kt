package com.medsy.data.offer.mapper

import com.medsy.data.offer.remote.dto.CreateOfferItemDto
import com.medsy.data.offer.remote.dto.CreateOfferRequestDto
import com.medsy.data.offer.remote.dto.OfferDto
import com.medsy.data.offer.remote.dto.OfferItemDto
import com.medsy.data.offer.remote.dto.PaginatedOffersDto
import com.medsy.domain.offer.model.CreateOfferItem
import com.medsy.domain.offer.model.CreateOfferRequest
import com.medsy.domain.offer.model.Offer
import com.medsy.domain.offer.model.OfferItem
import com.medsy.domain.offer.model.PaginatedOffers

fun OfferItemDto.toDomain(): OfferItem {
    return OfferItem(
        id = id ?: 0L,
        requestItemId = requestItemId ?: 0L,
        productId = productId ?: product?.id ?: 0L,
        productName = product?.productName ?: product?.name.orEmpty(),
        strength = product?.strength,
        packSize = product?.packSize,
        form = product?.form,
        imageUrl = product?.imageUrl,
        unitPrice = product?.price,
    )
}

fun OfferDto.toDomain(): Offer {
    return Offer(
        id = id ?: 0L,
        requestId = requestId ?: 0L,
        pharmacyId = pharmacyId ?: 0L,
        pharmacistId = pharmacistId ?: 0L,
        status = status.orEmpty(),
        distanceKm = distanceKm ?: 0.0,
        items = items?.map { it.toDomain() } ?: emptyList()
    )
}

fun PaginatedOffersDto.toDomain(): PaginatedOffers {
    return PaginatedOffers(
        content = content?.map { it.toDomain() } ?: emptyList(),
        pageNumber = pageNumber ?: 0,
        pageSize = pageSize ?: 20,
        totalElements = totalElements ?: 0L,
        totalPages = totalPages ?: 0,
        last = last ?: true
    )
}

fun CreateOfferItem.toDto(): CreateOfferItemDto {
    return CreateOfferItemDto(
        requestItemId = requestItemId,
        productId = productId
    )
}

fun CreateOfferRequest.toDto(): CreateOfferRequestDto {
    return CreateOfferRequestDto(
        items = items.map { it.toDto() }
    )
}
