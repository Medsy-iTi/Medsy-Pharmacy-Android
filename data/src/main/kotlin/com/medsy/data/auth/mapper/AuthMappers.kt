package com.medsy.data.auth.mapper

import com.medsy.data.auth.remote.dto.AuthResponseDto
import com.medsy.data.auth.remote.dto.RegisterRequestDto
import com.medsy.data.auth.remote.dto.UserResponseDto
import com.medsy.domain.auth.model.AuthSession
import com.medsy.domain.auth.model.AuthUser
import com.medsy.domain.auth.model.AuthUserRole
import com.medsy.domain.auth.model.AccountRole
import com.medsy.domain.auth.model.PharmacyAccount
import com.medsy.domain.auth.model.PharmacyApprovalStatus
import com.medsy.domain.auth.model.PharmacySession
import com.medsy.domain.auth.model.RegisterParams

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

fun RegisterParams.toDto(): RegisterRequestDto = RegisterRequestDto(
    email = email.trim(),
    phoneNumber = phoneNumber.trim(),
    firstName = firstName.trim(),
    lastName = lastName.trim(),
    password = password,
    role = role.name,
    homeAddress = homeAddress?.trim()?.takeIf(String::isNotEmpty),
    dob = dob.trim().takeIf(String::isNotEmpty),
    pharmacyId = pharmacyId,
)
