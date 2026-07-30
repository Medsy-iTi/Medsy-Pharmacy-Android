package com.medsy.domain.products.repository

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.products.model.ProductsPage

interface ProductsRepository {
    suspend fun searchProducts(
        keyword: String,
        page: Int,
        size: Int,
        sort: List<String>? = null
    ): MedsyResult<ProductsPage, MedsyError.Remote>
}
