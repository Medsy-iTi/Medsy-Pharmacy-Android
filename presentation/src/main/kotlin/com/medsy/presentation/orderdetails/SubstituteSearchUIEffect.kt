package com.medsy.presentation.orderdetails

sealed interface SubstituteSearchUIEffect {
    data object NavigateBack : SubstituteSearchUIEffect
    data class ReturnSubstitute(val productId: Long, val productName: String, val productPrice: Double, val productImage: String?) : SubstituteSearchUIEffect
}
