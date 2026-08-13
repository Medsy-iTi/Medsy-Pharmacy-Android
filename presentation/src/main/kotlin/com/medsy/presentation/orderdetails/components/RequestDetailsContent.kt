package com.medsy.presentation.orderdetails.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.medsy.presentation.R
import com.medsy.presentation.orderdetails.RequestDetailsUIState
import com.medsy.presentation.orderdetails.requestAgeLabel

@Composable
internal fun RequestDetailsContent(
    state: RequestDetailsUIState,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onCall: () -> Unit,
    onLocation: () -> Unit,
    onToggleItem: (Long) -> Unit,
    onAddSubstitute: (Long) -> Unit,
    onSendOffer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val request = requireNotNull(state.request)
    val canCreateOffer = state.canCreateOffer

    DetailsScaffold(
        titleRes = R.string.request_details_title,
        isRefreshing = state.isRefreshing,
        onRefresh = onRefresh,
        onBack = onBack,
        bottomBar = if (canCreateOffer) {
            {
                SendOfferBottomBar(
                    isSubmitting = state.isSubmitting,
                    enabled = state.selectedItems.isNotEmpty(),
                    onSendOfferClick = onSendOffer,
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                )
            }
        } else {
            null
        },
        modifier = modifier,
    ) {
        if (!canCreateOffer) {
            RequestAssignmentStatusCard(
                status = request.assignmentStatus,
                modifier = Modifier.padding(top = 16.dp),
            )
        }
        RequestInfoCard(
            id = request.id,
            createdLabel = requestAgeLabel(request.createdAt),
            customerName = request.customerName
                ?: stringResource(R.string.customer_default_format, request.customerId),
            customerPhone = request.customerPhone,
            customerAddress = request.deliveryAddress,
            onCallClick = onCall,
            onLocationClick = onLocation,
            modifier = Modifier.padding(top = 16.dp),
        )
        RequestedMedicinesSection(
            items = request.items,
            selectedItems = state.selectedItems,
            substitutes = state.substitutes,
            onItemCheckedChange = onToggleItem,
            onAddSubstituteClick = onAddSubstitute,
            readOnly = !canCreateOffer,
            modifier = Modifier.padding(top = 24.dp),
        )
        CommonDetailsSections(
            prescriptionUrl = request.prescriptionUrl,
            customerNotes = request.notes,
            paymentMethod = request.paymentMethod,
            total = if (canCreateOffer) {
                state.offerSubtotal
            } else {
                request.items.sumOf { it.unitPrice * it.quantity }
            },
            totalLabelRes = if (canCreateOffer) {
                R.string.request_details_offer_subtotal
            } else {
                R.string.request_details_total
            },
        )
    }
}
