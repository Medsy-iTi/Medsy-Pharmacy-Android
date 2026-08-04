package com.medsy.data.offer.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateOfferItemDto(
    @Json(name = "requestItemId") val requestItemId: Long,
    @Json(name = "productId") val productId: Long
)
