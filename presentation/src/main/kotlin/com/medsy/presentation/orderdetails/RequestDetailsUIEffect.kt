package com.medsy.presentation.orderdetails

sealed interface RequestDetailsUIEffect {
    data object NavigateBack : RequestDetailsUIEffect
    data class DialPhoneNumber(val phoneNumber: String) : RequestDetailsUIEffect
    data class OpenLocationOnMap(val latitude: Double, val longitude: Double) : RequestDetailsUIEffect
    data class ShowMessage(val messageRes: Int) : RequestDetailsUIEffect
    data class NavigateToSubstituteSearch(val itemId: Long) : RequestDetailsUIEffect
}
