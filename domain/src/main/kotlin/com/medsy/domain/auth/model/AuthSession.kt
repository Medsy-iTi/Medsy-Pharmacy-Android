package com.medsy.domain.auth.model

data class AuthSession(
    val accessToken: String,
    val refreshToken: String,
    val user: AuthUser,
)

data class AuthUser(
    val id: Long,
    val email: String,
    val firstName: String?,
    val lastName: String?,
    val role: AuthUserRole,
) {
    val displayName: String
        get() = listOfNotNull(firstName, lastName)
            .map(String::trim)
            .filter(String::isNotEmpty)
            .joinToString(separator = " ")
            .ifBlank { email }
}

enum class AuthUserRole {
    CUSTOMER,
    PHARMACIST,
    ADMIN,
    UNKNOWN,
}
