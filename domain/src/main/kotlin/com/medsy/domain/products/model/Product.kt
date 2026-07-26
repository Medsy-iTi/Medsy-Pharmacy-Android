package com.medsy.domain.products.model

data class Product(
    val id: Long,
    val name: String,
    val imageUrl: String?,
    val price: Double,
    val packSize: String?,
    val form: String?
)

data class ProductsPage(
    val content: List<Product>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalElements: Long,
    val totalPages: Int,
    val last: Boolean
)
