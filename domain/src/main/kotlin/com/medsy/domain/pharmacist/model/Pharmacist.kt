package com.medsy.domain.pharmacist.model

data class Pharmacist(
    val id: Long,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String?,
    val dob: String?,
    val homeAddress: String?,
    val pharmacyId: Long?,
    val pharmacyAdmin: Boolean
) {
    val fullName: String
        get() = "$firstName $lastName"
}
