package com.medsy.presentation.orderdetails

import com.medsy.presentation.orderdetails.model.Request
import com.medsy.domain.orders.model.PharmacyRequestAssignmentStatus

data class RequestDetailsUIState(
    val isLoading: Boolean = true,
    val request: Request? = null,
    val isSubmitting: Boolean = false,
    val pharmacistNotes: String = "",
    val selectedItems: Set<Long> = emptySet(),
    val assignmentStatus: PharmacyRequestAssignmentStatus = PharmacyRequestAssignmentStatus.Unknown,
) {
    val canCreateOffer: Boolean
        get() = assignmentStatus == PharmacyRequestAssignmentStatus.Pending
}
