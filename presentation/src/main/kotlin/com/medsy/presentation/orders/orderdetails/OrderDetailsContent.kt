package com.medsy.presentation.orders.orderdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.components.MedsyButton
import com.medsy.domain.orders.model.PharmacyOrder
import com.medsy.presentation.R
import com.medsy.presentation.orderdetails.components.CommonDetailsSections
import com.medsy.presentation.orderdetails.components.DetailsScaffold
import com.medsy.presentation.orderdetails.components.OrderStatusTimeline
import com.medsy.presentation.orderdetails.components.OrderedMedicinesSection
import com.medsy.presentation.orderdetails.components.RequestInfoCard
import com.medsy.presentation.orderdetails.orderDateLabel

@Composable
internal fun OrderDetailsContent(
    order: PharmacyOrder,
    isRefreshing: Boolean,
    isUpdatingStatus: Boolean,
    isStatusActionBlocked: Boolean,
    showStatusConfirmation: Boolean,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onCall: () -> Unit,
    onLocation: () -> Unit,
    onStatusAction: () -> Unit,
    onConfirmStatus: () -> Unit,
    onDismissStatus: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val statusAction = order.status.toOrderStatusActionUi()

    if (showStatusConfirmation && statusAction != null) {
        OrderStatusConfirmationDialog(
            action = statusAction,
            onConfirm = onConfirmStatus,
            onDismiss = onDismissStatus,
        )
    }

    DetailsScaffold(
        titleRes = R.string.order_details_title,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        onBack = onBack,
        bottomBar = if (statusAction != null && !isStatusActionBlocked) {
            {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp),
                ) {
                    MedsyButton(
                        onClick = onStatusAction,
                        isLoading = isUpdatingStatus,
                    ) {
                        Text(stringResource(statusAction.labelRes))
                    }
                }
            }
        } else {
            null
        },
        modifier = modifier,
    ) {
        OrderStatusTimeline(order.status, Modifier.padding(top = 16.dp))
        if (order.status.isWaitingForPatient) {
            Text(
                text = stringResource(R.string.order_details_waiting_for_patient),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            )
        }
        RequestInfoCard(
            id = order.id,
            createdLabel = orderDateLabel(order.createdAt),
            customerName = order.customerName
                ?: stringResource(R.string.customer_default_format, order.customerId),
            customerPhone = order.customerPhone,
            customerAddress = order.deliveryAddress,
            onCallClick = onCall,
            onLocationClick = onLocation,
            modifier = Modifier.padding(top = 16.dp),
        )
        OrderedMedicinesSection(order.items, Modifier.padding(top = 24.dp))
        CommonDetailsSections(
            prescriptionUrl = order.prescriptionUrl,
            customerNotes = order.customerNotes,
            paymentMethod = order.paymentMethod,
            total = order.total,
            totalLabelRes = R.string.request_details_total,
        )
    }
}

@Composable
private fun OrderStatusConfirmationDialog(
    action: OrderStatusActionUi,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(action.titleRes)) },
        text = { Text(stringResource(action.messageRes)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(action.labelRes))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.order_details_status_action_cancel))
            }
        },
    )
}
