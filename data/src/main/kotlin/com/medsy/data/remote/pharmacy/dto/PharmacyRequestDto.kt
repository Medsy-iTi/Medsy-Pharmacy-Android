package com.medsy.data.remote.pharmacy.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PharmacyRequestDto(
    @Json(name = "name") val name: String,
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double,
    @Json(name = "address") val address: String,
    @Json(name = "phoneNumber") val phoneNumber: String
)
