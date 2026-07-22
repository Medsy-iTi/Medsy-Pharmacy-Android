package com.medsy.data.pharmacy.mapper

import com.medsy.data.pharmacy.remote.dto.PharmacyMineDto
import com.medsy.data.pharmacy.remote.dto.PharmacyPharmacistDto
import com.medsy.data.pharmacy.remote.dto.PharmacyResponseDto
import com.medsy.domain.pharmacy.model.MyPharmacy
import com.medsy.domain.pharmacy.model.PharmacyPharmacist

fun PharmacyMineDto.toDomain(): MyPharmacy = MyPharmacy(
    id = id,
    name = name,
    latitude = latitude ?: 0.0,
    longitude = longitude ?: 0.0,
    address = address,
    phoneNumber = phoneNumber,
    isAdmin = isAdmin,
    pharmacists = pharmacists.map { it.toDomain() }
)

fun PharmacyResponseDto.toDomain(): MyPharmacy = MyPharmacy(
    id = id,
    name = name,
    latitude = latitude ?: 0.0,
    longitude = longitude ?: 0.0,
    address = address,
    phoneNumber = phoneNumber,
    isAdmin = true,
)

fun PharmacyPharmacistDto.toDomain(): PharmacyPharmacist = PharmacyPharmacist(
    id = id,
    firstName = firstName,
    lastName = lastName,
    phoneNumber = phoneNumber,
    email = email,
    isAdmin = isAdmin
)
