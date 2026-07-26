package com.medsy.data.products.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductDto(
    val id: Long,
    val name: String,
    val imageUrl: String?,
    val price: Double,
    val packSize: String?,
    val form: String?
)

@JsonClass(generateAdapter = true)
data class ProductsPageDto(
    val content: List<ProductDto>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalElements: Long,
    val totalPages: Int,
    val last: Boolean
)
