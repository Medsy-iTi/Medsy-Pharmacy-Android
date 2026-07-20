package com.medsy.data.pharmacy.mapper

import com.medsy.data.pharmacy.remote.dto.PharmacyMineDto
import com.medsy.data.pharmacy.remote.dto.PharmacyResponseDto
import com.medsy.domain.pharmacy.model.MyPharmacy

fun PharmacyMineDto.toDomain(): MyPharmacy = MyPharmacy(
    id = id,
    name = name,
    isAdmin = isAdmin,
)

fun PharmacyResponseDto.toDomain(): MyPharmacy = MyPharmacy(
    id = id,
    name = name,
    isAdmin = true,
)
