package com.medsy.presentation.orderdetails.offer

sealed interface OfferDetailsUIIntent {
    data class Load(val offerId: Long) : OfferDetailsUIIntent
    data object Retry : OfferDetailsUIIntent
    data object BackClicked : OfferDetailsUIIntent
}
