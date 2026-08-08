package com.medsy.presentation.orderdetails.offer

import com.medsy.domain.offer.model.Offer

data class OfferDetailsUIState(
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
    val offer: Offer? = null,
)
