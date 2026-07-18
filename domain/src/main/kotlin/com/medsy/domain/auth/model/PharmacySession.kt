package com.medsy.domain.auth.model

data class PharmacySession(
    val accessToken: String,
    val refreshToken: String,
    val account: PharmacyAccount,
)

data class PharmacyAccount(
    val id: Long,
    val displayName: String,
    val role: AccountRole = AccountRole.PHARMACIST,
    val approvalStatus: PharmacyApprovalStatus,
)

enum class AccountRole {
    PHARMACIST,
}

enum class PharmacyApprovalStatus {
    PendingApproval,
    Approved,
    Rejected,
    Suspended,
}
