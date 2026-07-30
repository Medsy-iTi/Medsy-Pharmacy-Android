package com.medsy.presentation.orderdetails

import com.medsy.domain.products.model.Product

data class SubstituteSearchUIState(
    val query: String = "",
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val error: Int? = null
)
