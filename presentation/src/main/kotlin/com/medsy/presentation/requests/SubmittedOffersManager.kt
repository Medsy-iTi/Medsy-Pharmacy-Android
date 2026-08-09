package com.medsy.presentation.requests

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

data class SubmittedOfferData(
    val productImages: List<String?>,
    val total: Double,
)

@Singleton
class SubmittedOffersManager @Inject constructor() {
    private val _submittedRequestIds = MutableStateFlow<Set<Long>>(emptySet())
    val submittedRequestIds: StateFlow<Set<Long>> = _submittedRequestIds.asStateFlow()

    private val _submittedOfferData = MutableStateFlow<Map<Long, SubmittedOfferData>>(emptyMap())
    val submittedOfferData: StateFlow<Map<Long, SubmittedOfferData>> = _submittedOfferData.asStateFlow()

    fun addSubmittedRequestId(id: Long) {
        _submittedRequestIds.update { it + id }
    }

    fun addSubmittedOffer(id: Long, data: SubmittedOfferData) {
        _submittedRequestIds.update { it + id }
        _submittedOfferData.update { it + (id to data) }
    }
}
