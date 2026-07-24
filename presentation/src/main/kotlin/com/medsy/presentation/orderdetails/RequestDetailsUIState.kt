package com.medsy.presentation.orderdetails

import com.medsy.presentation.orderdetails.model.Request

data class RequestDetailsUIState(
    val isLoading: Boolean = true,
    val request: Request? = null,
    val isSubmitting: Boolean = false,
    val pharmacistNotes: String = "",
)