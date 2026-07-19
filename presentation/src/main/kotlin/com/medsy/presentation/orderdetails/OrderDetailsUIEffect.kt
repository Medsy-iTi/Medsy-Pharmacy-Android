package com.medsy.presentation.orderdetails

sealed interface OrderDetailsUIEffect {
    data object NavigateBack : OrderDetailsUIEffect
    data class DialPhoneNumber(val phoneNumber: String) : OrderDetailsUIEffect
    data object OpenLocationOnMap : OrderDetailsUIEffect
    data object OpenPaymentSummary : OrderDetailsUIEffect
    data object OpenCustomerChat : OrderDetailsUIEffect
    data class ShowMessage(val messageRes: Int) : OrderDetailsUIEffect
}
