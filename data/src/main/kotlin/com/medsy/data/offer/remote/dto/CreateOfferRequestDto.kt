package com.medsy.data.offer.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateOfferRequestDto(
    @Json(name = "items") val items: List<CreateOfferItemDto>
)
