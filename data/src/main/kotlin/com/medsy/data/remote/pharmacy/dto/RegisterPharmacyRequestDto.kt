package com.medsy.data.remote.pharmacy.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisterPharmacyRequestDto(
    @Json(name = "pharmacyRequest") val pharmacyRequest: PharmacyRequestDto,
    @Json(name = "license") val license: String
)

