package com.medsy.presentation.auth.otp

sealed interface OtpUIIntent {
    data class CodeChanged(val value: String) : OtpUIIntent
    data object Submit : OtpUIIntent
    data object Resend : OtpUIIntent
    data object Tick : OtpUIIntent
}
