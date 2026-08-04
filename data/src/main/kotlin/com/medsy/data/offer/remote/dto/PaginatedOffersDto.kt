package com.medsy.data.offer.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PaginatedOffersDto(
    @Json(name = "content") val content: List<OfferDto>?,
    @Json(name = "pageNumber") val pageNumber: Int?,
    @Json(name = "pageSize") val pageSize: Int?,
    @Json(name = "totalElements") val totalElements: Long?,
    @Json(name = "totalPages") val totalPages: Int?,
    @Json(name = "last") val last: Boolean?
)
