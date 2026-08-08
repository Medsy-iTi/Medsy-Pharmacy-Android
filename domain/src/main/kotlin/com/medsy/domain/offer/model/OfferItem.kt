package com.medsy.domain.offer.model

data class OfferItem(
    val id: Long,
    val requestItemId: Long,
    val productId: Long,
    val productName: String,
    val strength: String?,
    val packSize: String?,
    val form: String?,
    val imageUrl: String?,
    val unitPrice: Double?,
)
