package com.medsy.data.pharmacist.mapper

import com.medsy.data.pharmacist.remote.dto.PharmacistDto
import com.medsy.domain.pharmacist.model.Pharmacist

fun PharmacistDto.toDomain(): Pharmacist {
    return Pharmacist(
        id = id,
        email = email,
        firstName = firstName,
        lastName = lastName,
        phoneNumber = phoneNumber,
        dob = dob,
        homeAddress = homeAddress,
        pharmacyId = pharmacyId,
        pharmacyAdmin = pharmacyAdmin
    )
}
