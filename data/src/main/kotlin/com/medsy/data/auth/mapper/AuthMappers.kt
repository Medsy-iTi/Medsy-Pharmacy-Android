package com.medsy.data.auth.mapper

import com.medsy.data.auth.remote.dto.AuthResponseDto
import com.medsy.data.auth.remote.dto.UserResponseDto
import com.medsy.domain.auth.model.AuthSession
import com.medsy.domain.auth.model.AuthUser
import com.medsy.domain.auth.model.AuthUserRole
import com.medsy.domain.auth.model.AccountRole
import com.medsy.domain.auth.model.PharmacyAccount
import com.medsy.domain.auth.model.PharmacyApprovalStatus
import com.medsy.domain.auth.model.PharmacySession

fun AuthResponseDto.toDomain(): AuthSession = AuthSession(
    accessToken = accessToken,
    refreshToken = refreshToken,
    user = user.toDomain(),
)

fun AuthSession.toPharmacySession(
    approvalStatus: PharmacyApprovalStatus,
): PharmacySession = PharmacySession(
    accessToken = accessToken,
    refreshToken = refreshToken,
    account = PharmacyAccount(
        id = user.id,
        displayName = user.displayName,
        role = AccountRole.PHARMACIST,
        approvalStatus = approvalStatus,
    ),
)

private fun UserResponseDto.toDomain(): AuthUser = AuthUser(
    id = id,
    email = email,
    firstName = firstName,
    lastName = lastName,
    role = runCatching { AuthUserRole.valueOf(role) }.getOrDefault(AuthUserRole.UNKNOWN),
)
