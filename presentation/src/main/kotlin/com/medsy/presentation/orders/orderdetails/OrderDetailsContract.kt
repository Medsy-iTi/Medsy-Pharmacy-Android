package com.medsy.presentation.orders.orderdetails

import com.medsy.presentation.orderdetails.model.Request

data class OrderDetailsUIState(
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
    val order: Request? = null,
    val status: String = "",
    val minutesAgo: Int? = null,
)

sealed interface OrderDetailsUIIntent {
    data class Load(val orderId: Long) : OrderDetailsUIIntent
    data object Retry : OrderDetailsUIIntent
    data object BackClicked : OrderDetailsUIIntent
    data object CallCustomerClicked : OrderDetailsUIIntent
    data object OpenLocationClicked : OrderDetailsUIIntent
}

sealed interface OrderDetailsUIEffect {
    data object NavigateBack : OrderDetailsUIEffect
    data class DialPhoneNumber(val phoneNumber: String) : OrderDetailsUIEffect
    data class OpenLocation(val latitude: Double, val longitude: Double) : OrderDetailsUIEffect
}
