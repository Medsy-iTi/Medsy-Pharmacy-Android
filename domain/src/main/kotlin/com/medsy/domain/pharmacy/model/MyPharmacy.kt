package com.medsy.domain.pharmacy.model

data class MyPharmacy(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val address: String?,
    val phoneNumber: String?,
    val isAdmin: Boolean,
    val pharmacists: List<PharmacyPharmacist> = emptyList()
)

data class PharmacyPharmacist(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String?,
    val email: String,
    val isAdmin: Boolean
) {
    val fullName: String
        get() = "$firstName $lastName"
}
