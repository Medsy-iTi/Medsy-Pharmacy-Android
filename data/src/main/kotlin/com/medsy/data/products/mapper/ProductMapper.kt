package com.medsy.data.products.mapper

import com.medsy.data.products.remote.dto.ProductDto
import com.medsy.data.products.remote.dto.ProductsPageDto
import com.medsy.domain.products.model.Product
import com.medsy.domain.products.model.ProductsPage

fun ProductDto.toDomain(): Product = Product(
    id = id,
    name = name,
    imageUrl = imageUrl,
    price = price,
    packSize = packSize,
    form = form
)

fun ProductsPageDto.toDomain(): ProductsPage = ProductsPage(
    content = content.map { it.toDomain() },
    pageNumber = pageNumber,
    pageSize = pageSize,
    totalElements = totalElements,
    totalPages = totalPages,
    last = last
)
