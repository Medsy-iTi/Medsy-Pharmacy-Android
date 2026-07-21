package com.medsy.data.pharmacist.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdatePharmacistRequestDto(
    val firstName: String? = null,
    val lastName: String? = null,
    val homeAddress: String? = null,
    val dob: String? = null
)
