package com.medsy.presentation.orderdetails

sealed interface RequestDetailsUIEffect {
    data object NavigateBack : RequestDetailsUIEffect
    data class DialPhoneNumber(val phoneNumber: String) : RequestDetailsUIEffect
    data object OpenLocationOnMap : RequestDetailsUIEffect
    data object OpenPaymentSummary : RequestDetailsUIEffect
    data object OpenCustomerChat : RequestDetailsUIEffect
    data class ShowMessage(val messageRes: Int) : RequestDetailsUIEffect
}
