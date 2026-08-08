package com.medsy.presentation.orderdetails.offer

sealed interface OfferDetailsUIEffect {
    data object NavigateBack : OfferDetailsUIEffect
}
