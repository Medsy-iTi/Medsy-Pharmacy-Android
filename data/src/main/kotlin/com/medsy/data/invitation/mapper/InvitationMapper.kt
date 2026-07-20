package com.medsy.data.invitation.mapper

import com.medsy.data.invitation.remote.dto.InvitationDto
import com.medsy.domain.invitation.model.PharmacyInvitation

fun InvitationDto.toDomain(): PharmacyInvitation {
    return PharmacyInvitation(
        id = id,
        pharmacyId = pharmacyId,
        pharmacyName = pharmacyName,
        pharmacistId = pharmacistId,
        pharmacistFirstName = pharmacistFirstName,
        pharmacistLastName = pharmacistLastName,
        status = status,
        createdAt = createdAt
    )
}
