package com.medsy.domain.pharmacy.model

data class UpdatePharmacyParams(
    val pharmacyId: Long,
    val name: String?,
    val latitude: Double?,
    val longitude: Double?,
    val address: String?,
    val phoneNumber: String?
)
