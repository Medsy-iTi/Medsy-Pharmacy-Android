package com.medsy.presentation.orderdetails.substitute

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

data class SubstituteResult(
    val requestItemId: Long,
    val productId: Long,
    val productName: String,
    val productPrice: Double,
    val productImage: String? = null
)

@Singleton
class SubstituteResultManager @Inject constructor() {
    private val _results = MutableSharedFlow<SubstituteResult>(extraBufferCapacity = 1)
    val results = _results.asSharedFlow()

    fun sendResult(result: SubstituteResult) {
        _results.tryEmit(result)
    }
}
