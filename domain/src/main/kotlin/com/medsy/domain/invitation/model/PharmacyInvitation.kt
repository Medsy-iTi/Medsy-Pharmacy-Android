package com.medsy.domain.invitation.model

data class PharmacyInvitation(
    val id: Long,
    val pharmacyId: Long,
    val pharmacyName: String,
    val pharmacistId: Long,
    val pharmacistFirstName: String,
    val pharmacistLastName: String,
    val status: String,
    val createdAt: String
)
