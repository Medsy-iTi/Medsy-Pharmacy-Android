package com.medsy.domain.offer.model

data class PaginatedOffers(
    val content: List<Offer>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalElements: Long,
    val totalPages: Int,
    val last: Boolean
)
