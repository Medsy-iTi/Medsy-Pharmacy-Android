package com.medsy.presentation.auth.otp

data class OtpUIState(
    val email: String = "",
    val code: String = "",
    val countdown: Int = 300,
    val isLoading: Boolean = false,
    val hasError: Boolean = false
)
