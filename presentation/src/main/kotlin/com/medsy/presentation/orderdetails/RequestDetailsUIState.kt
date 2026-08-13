package com.medsy.presentation.orderdetails

import com.medsy.domain.orders.model.PharmacyRequest
import com.medsy.domain.orders.model.PharmacyRequestAssignmentStatus
import com.medsy.domain.orders.model.PharmacyRequestStatus

data class SubstituteDraft(
    val productId: Long,
    val productName: String,
    val productPrice: Double,
    val productImage: String?,
)

data class RequestDetailsUIState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val hasError: Boolean = false,
    val request: PharmacyRequest? = null,
    val isSubmitting: Boolean = false,
    val selectedItems: Set<Long> = emptySet(),
    val substitutes: Map<Long, SubstituteDraft> = emptyMap(),
) {
    val canCreateOffer: Boolean
        get() = request?.let {
            it.requestStatus == PharmacyRequestStatus.Searching &&
                it.assignmentStatus == PharmacyRequestAssignmentStatus.Pending
        } == true

    val offerSubtotal: Double
        get() = request?.items
            ?.filter { it.id in selectedItems }
            ?.sumOf { item ->
                (substitutes[item.id]?.productPrice ?: item.unitPrice) * item.quantity
            }
            ?: 0.0
}
