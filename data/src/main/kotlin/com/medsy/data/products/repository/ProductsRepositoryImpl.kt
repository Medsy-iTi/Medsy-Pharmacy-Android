package com.medsy.data.products.repository

import com.medsy.data.products.mapper.toDomain
import com.medsy.data.remote.api.ApiService
import com.medsy.data.remote.network.safeApiCall
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.map
import com.medsy.domain.products.model.ProductsPage
import com.medsy.domain.products.repository.ProductsRepository
import javax.inject.Inject

class ProductsRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : ProductsRepository {

    override suspend fun searchProducts(
        keyword: String,
        page: Int,
        size: Int,
        sort: List<String>?
    ): MedsyResult<ProductsPage, MedsyError.Remote> {
        return safeApiCall { apiService.searchProducts(keyword, page, size, sort) }
            .map { it.toDomain() }
    }
}
