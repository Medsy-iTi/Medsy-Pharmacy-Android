package com.medsy.presentation.auth.login

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val emailErrorRes: Int? = null,
    val passwordErrorRes: Int? = null,
)
