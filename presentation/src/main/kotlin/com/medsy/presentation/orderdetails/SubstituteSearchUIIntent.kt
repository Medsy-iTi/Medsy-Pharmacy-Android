package com.medsy.presentation.orderdetails

sealed interface SubstituteSearchUIIntent {
    data class SearchQueryChanged(val query: String) : SubstituteSearchUIIntent
    data class ProductSelected(val productId: Long, val productName: String, val productPrice: Double, val productImage: String?) : SubstituteSearchUIIntent
    data object BackClicked : SubstituteSearchUIIntent
}
