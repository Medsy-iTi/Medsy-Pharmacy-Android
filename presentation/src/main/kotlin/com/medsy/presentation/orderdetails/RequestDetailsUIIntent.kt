package com.medsy.presentation.orderdetails

sealed interface RequestDetailsUIIntent {
    data class LoadRequest(val requestId: Long) : RequestDetailsUIIntent
    data object Refresh : RequestDetailsUIIntent
    data object Retry : RequestDetailsUIIntent
    data object BackClicked : RequestDetailsUIIntent
    data object CallCustomerClicked : RequestDetailsUIIntent
    data object OpenLocationClicked : RequestDetailsUIIntent
    data object SendOfferClicked : RequestDetailsUIIntent
    data class ToggleItemSelection(val itemId: Long) : RequestDetailsUIIntent
    data class AddSubstituteClicked(val itemId: Long) : RequestDetailsUIIntent
    data class SubstituteSelected(
        val itemId: Long,
        val productId: Long,
        val productName: String,
        val productPrice: Double,
        val productImage: String?,
    ) : RequestDetailsUIIntent
}
