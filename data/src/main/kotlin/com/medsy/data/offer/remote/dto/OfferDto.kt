package com.medsy.data.offer.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OfferDto(
    @Json(name = "id") val id: Long?,
    @Json(name = "requestId") val requestId: Long?,
    @Json(name = "pharmacyId") val pharmacyId: Long?,
    @Json(name = "pharmacistId") val pharmacistId: Long?,
    @Json(name = "status") val status: String?,
    @Json(name = "distanceKm") val distanceKm: Double?,
    @Json(name = "items") val items: List<OfferItemDto>?
)
