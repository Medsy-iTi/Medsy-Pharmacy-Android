package com.medsy.presentation.orders.orderdetails

import com.medsy.domain.orders.model.PharmacyOrder

data class OrderDetailsUIState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val hasError: Boolean = false,
    val order: PharmacyOrder? = null,
    val showReadyConfirmation: Boolean = false,
    val isMarkingReady: Boolean = false,
    val isReadyActionBlocked: Boolean = false,
)
