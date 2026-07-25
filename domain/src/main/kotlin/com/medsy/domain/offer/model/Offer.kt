package com.medsy.domain.offer.model

data class Offer(
    val id: Long,
    val requestId: Long,
    val pharmacyId: Long,
    val pharmacistId: Long,
    val status: String,
    val distanceKm: Double,
    val items: List<OfferItem>
)
