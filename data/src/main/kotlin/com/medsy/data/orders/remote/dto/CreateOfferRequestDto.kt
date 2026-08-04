package com.medsy.data.orders.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateOfferRequestDto(
    val items: List<OfferItemDto>
)

@JsonClass(generateAdapter = true)
data class OfferItemDto(
    val requestItemId: Long,
    val productId: Long
)
