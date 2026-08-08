package com.medsy.presentation.orders.orderdetails

import com.medsy.presentation.orderdetails.model.Request

data class OrderDetailsUIState(
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
    val order: Request? = null,
    val status: String = "",
    val minutesAgo: Int? = null,
)
