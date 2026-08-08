package com.medsy.data.common.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductSummaryDto(
    val id: Long? = null,
    val name: String? = null,
    val productName: String? = null,
    val strength: String? = null,
    val packSize: String? = null,
    val form: String? = null,
    val price: Double? = null,
    val scientificName: String? = null,
    val company: String? = null,
    val route: String? = null,
    val description: String? = null,
    val imageUrl: String? = null,
)
