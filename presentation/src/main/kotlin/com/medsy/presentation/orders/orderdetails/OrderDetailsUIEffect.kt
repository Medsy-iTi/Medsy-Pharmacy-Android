package com.medsy.presentation.orders.orderdetails

sealed interface OrderDetailsUIEffect {
    data object NavigateBack : OrderDetailsUIEffect
    data class DialPhoneNumber(val phoneNumber: String) : OrderDetailsUIEffect
    data class OpenLocation(val latitude: Double, val longitude: Double) : OrderDetailsUIEffect
    data class ShowMessage(val messageRes: Int) : OrderDetailsUIEffect
}
