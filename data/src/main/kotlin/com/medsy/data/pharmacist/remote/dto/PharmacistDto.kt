package com.medsy.data.pharmacist.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PharmacistDto(
    @Json(name = "id") val id: Long,
    @Json(name = "email") val email: String,
    @Json(name = "firstName") val firstName: String,
    @Json(name = "lastName") val lastName: String,
    @Json(name = "phoneNumber") val phoneNumber: String?,
    @Json(name = "dob") val dob: String?,
    @Json(name = "homeAddress") val homeAddress: String?,
    @Json(name = "pharmacyId") val pharmacyId: Long?,
    @Json(name = "pharmacyAdmin") val pharmacyAdmin: Boolean
)
