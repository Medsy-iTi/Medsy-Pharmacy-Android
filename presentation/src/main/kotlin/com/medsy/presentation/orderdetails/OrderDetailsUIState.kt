package com.medsy.presentation.orderdetails

data class OrderDetailsUIState(
    val isLoading: Boolean = true,
    val order: Order? = null,
    val isSubmitting: Boolean = false,
)