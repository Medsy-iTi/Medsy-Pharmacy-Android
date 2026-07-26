package com.medsy.domain.products.usecase

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.products.model.ProductsPage
import com.medsy.domain.products.repository.ProductsRepository
import javax.inject.Inject

class SearchProductsUseCase @Inject constructor(
    private val repository: ProductsRepository
) {
    suspend operator fun invoke(
        keyword: String,
        page: Int = 0,
        size: Int = 20,
        sort: List<String>? = null
    ): MedsyResult<ProductsPage, MedsyError.Remote> =
        repository.searchProducts(keyword, page, size, sort)
}
