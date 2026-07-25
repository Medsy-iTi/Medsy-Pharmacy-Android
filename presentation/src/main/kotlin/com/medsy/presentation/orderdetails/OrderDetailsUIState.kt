package com.medsy.presentation.orderdetails

import com.medsy.presentation.orderdetails.model.Order

data class OrderDetailsUIState(
    val isLoading: Boolean = true,
    val order: Order? = null,
    val isSubmitting: Boolean = false,
    val pharmacistNotes: String = "",
    val selectedItems: Set<Long> = emptySet(),
)