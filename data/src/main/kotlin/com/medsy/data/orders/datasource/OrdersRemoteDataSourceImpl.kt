package com.medsy.data.orders.datasource

import com.medsy.data.orders.model.OrderDetailsDto
import com.medsy.data.orders.model.OrderPageResponseDto
import com.medsy.data.remote.api.ApiService
import javax.inject.Inject

class OrdersRemoteDataSourceImpl @Inject constructor(
    private val apiService: ApiService
) : OrdersRemoteDataSource {

    override suspend fun getCurrentPharmacyRequests(
        page: Int,
        size: Int,
        sort: List<String>?
    ): OrderPageResponseDto {
        val response = apiService.getCurrentPharmacyRequests(page, size, sort)
        val responseBody = response.body()

        if (response.isSuccessful && responseBody?.success == true) {
            return responseBody.data ?: throw Exception("Pharmacy requests data is null")
        } else {
            throw Exception(responseBody?.message ?: "Failed to fetch current pharmacy requests")
        }
    }
}