package com.medsy.presentation.requests

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubmittedOffersManager @Inject constructor() {
    private val _submittedRequestIds = MutableStateFlow<Set<Long>>(emptySet())
    val submittedRequestIds: StateFlow<Set<Long>> = _submittedRequestIds.asStateFlow()

    fun addSubmittedRequestId(id: Long) {
        _submittedRequestIds.update { it + id }
    }
}
