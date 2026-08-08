package com.medsy.presentation.orderdetails.offer

import com.medsy.domain.offer.model.Offer

data class OfferDetailsUIState(
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
    val offer: Offer? = null,
)

sealed interface OfferDetailsUIIntent {
    data class Load(val offerId: Long) : OfferDetailsUIIntent
    data object Retry : OfferDetailsUIIntent
    data object BackClicked : OfferDetailsUIIntent
}

sealed interface OfferDetailsUIEffect {
    data object NavigateBack : OfferDetailsUIEffect
}
