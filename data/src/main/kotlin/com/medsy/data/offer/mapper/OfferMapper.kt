package com.medsy.data.offer.mapper

import com.medsy.data.offer.remote.dto.CreateOfferItemDto
import com.medsy.data.offer.remote.dto.CreateOfferRequestDto
import com.medsy.domain.offer.model.CreateOfferItem
import com.medsy.domain.offer.model.CreateOfferRequest
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
