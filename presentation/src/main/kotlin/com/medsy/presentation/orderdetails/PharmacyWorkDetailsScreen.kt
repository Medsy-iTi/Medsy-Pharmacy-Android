package com.medsy.presentation.orderdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.components.MedsyButton
import com.medsy.domain.orders.model.PharmacyOrder
import com.medsy.domain.orders.model.PharmacyOrderStatus
import com.medsy.domain.orders.model.PharmacyRequest
import com.medsy.presentation.R
import com.medsy.presentation.orderdetails.components.CustomerNotesSection
import com.medsy.presentation.orderdetails.components.OrderStatusTimeline
import com.medsy.presentation.orderdetails.components.OrderedMedicinesSection
import com.medsy.presentation.orderdetails.components.PaymentMethodSection
import com.medsy.presentation.orderdetails.components.PrescriptionImageSection
import com.medsy.presentation.orderdetails.components.RequestActionButtons
import com.medsy.presentation.orderdetails.components.RequestAssignmentStatusCard
import com.medsy.presentation.orderdetails.components.RequestDetailsTopBar
import com.medsy.presentation.orderdetails.components.RequestInfoCard
import com.medsy.presentation.orderdetails.components.RequestTotalSummaryRow
import com.medsy.presentation.orderdetails.components.RequestedMedicinesSection
import com.medsy.presentation.orderdetails.components.ZoomableImageDialog
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PharmacyWorkDetailsScreen(
    request: PharmacyRequest,
    isRefreshing: Boolean,
    isSubmitting: Boolean,
    selectedItems: Set<Long>,
    substitutes: Map<Long, SubstituteDraft>,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onCall: () -> Unit,
    onLocation: () -> Unit,
    onToggleItem: (Long) -> Unit,
    onAddSubstitute: (Long) -> Unit,
    onSendOffer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PharmacyWorkDetailsLayout(
        titleRes = R.string.request_details_title,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        onBack = onBack,
        bottomBar = if (request.assignmentStatus == com.medsy.domain.orders.model.PharmacyRequestAssignmentStatus.Pending) {
            {
                RequestActionButtons(
                    isSubmitting = isSubmitting,
                    enabled = selectedItems.isNotEmpty(),
                    onAcceptClick = onSendOffer,
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                )
            }
        } else null,
        modifier = modifier,
    ) {
        if (request.assignmentStatus != com.medsy.domain.orders.model.PharmacyRequestAssignmentStatus.Pending) {
            RequestAssignmentStatusCard(request.assignmentStatus, Modifier.padding(top = 16.dp))
        }
        RequestInfoCard(
            id = request.id,
            createdLabel = request.createdAt.requestAgeLabel(),
            customerName = request.customerName ?: stringResource(R.string.customer_default_format, request.customerId),
            customerPhone = request.customerPhone,
            customerAddress = request.deliveryAddress,
            onCallClick = onCall,
            onLocationClick = onLocation,
            modifier = Modifier.padding(top = 16.dp),
        )
        RequestedMedicinesSection(
            items = request.items,
            selectedItems = selectedItems,
            substitutes = substitutes,
            onItemCheckedChange = onToggleItem,
            onAddSubstituteClick = onAddSubstitute,
            readOnly = request.assignmentStatus != com.medsy.domain.orders.model.PharmacyRequestAssignmentStatus.Pending,
            modifier = Modifier.padding(top = 24.dp),
        )
        CommonDetailSections(
            prescriptionUrl = request.prescriptionUrl,
            customerNotes = request.notes,
            paymentMethod = request.paymentMethod,
            total = if (request.assignmentStatus == com.medsy.domain.orders.model.PharmacyRequestAssignmentStatus.Pending) {
                request.items.filter { it.id in selectedItems }.sumOf { item ->
                    (substitutes[item.id]?.productPrice ?: item.unitPrice) * item.quantity
                }
            } else request.items.sumOf { it.unitPrice * it.quantity },
            totalLabelRes = if (
                request.assignmentStatus == com.medsy.domain.orders.model.PharmacyRequestAssignmentStatus.Pending
            ) R.string.request_details_offer_subtotal else R.string.request_details_total,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PharmacyWorkDetailsScreen(
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
    val actionLabelRes = order.status.actionLabelRes()
    if (showStatusConfirmation && actionLabelRes != null) {
        AlertDialog(
            onDismissRequest = onDismissStatus,
            title = { Text(stringResource(order.status.actionTitleRes())) },
            text = { Text(stringResource(order.status.actionMessageRes())) },
            confirmButton = {
                TextButton(onClick = onConfirmStatus) { Text(stringResource(actionLabelRes)) }
            },
            dismissButton = {
                TextButton(onClick = onDismissStatus) { Text(stringResource(R.string.order_details_status_action_cancel)) }
            },
        )
    }
    PharmacyWorkDetailsLayout(
        titleRes = R.string.order_details_title,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        onBack = onBack,
        bottomBar = if (actionLabelRes != null && !isStatusActionBlocked) {
            {
                Box(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface).padding(16.dp)) {
                    MedsyButton(onClick = onStatusAction, isLoading = isUpdatingStatus) {
                        Text(stringResource(actionLabelRes))
                    }
                }
            }
        } else null,
        modifier = modifier,
    ) {
        OrderStatusTimeline(order.status, Modifier.padding(top = 16.dp))
        if (order.status.isWaitingForPatient) {
            Text(
                stringResource(R.string.order_details_waiting_for_patient),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            )
        }
        RequestInfoCard(
            id = order.id,
            createdLabel = order.createdAt.orderDateLabel(),
            customerName = order.customerName ?: stringResource(R.string.customer_default_format, order.customerId),
            customerPhone = order.customerPhone,
            customerAddress = order.deliveryAddress,
            onCallClick = onCall,
            onLocationClick = onLocation,
            modifier = Modifier.padding(top = 16.dp),
        )
        OrderedMedicinesSection(order.items, Modifier.padding(top = 24.dp))
        CommonDetailSections(
            prescriptionUrl = order.prescriptionUrl,
            customerNotes = order.customerNotes,
            paymentMethod = order.paymentMethod,
            total = order.total,
            totalLabelRes = R.string.request_details_total,
        )
    }
}

private fun PharmacyOrderStatus.actionLabelRes(): Int? = when (this) {
    PharmacyOrderStatus.Preparing -> R.string.order_details_mark_ready_action
    PharmacyOrderStatus.ReadyForDelivery -> R.string.order_details_start_delivery_action
    PharmacyOrderStatus.ReadyForPickup -> R.string.order_details_mark_collected_action
    PharmacyOrderStatus.OutForDelivery -> R.string.order_details_mark_delivered_action
    else -> null
}

private fun PharmacyOrderStatus.actionTitleRes(): Int = when (this) {
    PharmacyOrderStatus.Preparing -> R.string.order_details_mark_ready_title
    PharmacyOrderStatus.ReadyForDelivery -> R.string.order_details_start_delivery_title
    PharmacyOrderStatus.ReadyForPickup -> R.string.order_details_mark_collected_title
    PharmacyOrderStatus.OutForDelivery -> R.string.order_details_mark_delivered_title
    else -> R.string.order_details_title
}

private fun PharmacyOrderStatus.actionMessageRes(): Int = when (this) {
    PharmacyOrderStatus.Preparing -> R.string.order_details_mark_ready_message
    PharmacyOrderStatus.ReadyForDelivery -> R.string.order_details_start_delivery_message
    PharmacyOrderStatus.ReadyForPickup -> R.string.order_details_mark_collected_message
    PharmacyOrderStatus.OutForDelivery -> R.string.order_details_mark_delivered_message
    else -> R.string.order_details_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PharmacyWorkDetailsLayout(
    titleRes: Int,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onBack: () -> Unit,
    bottomBar: (@Composable () -> Unit)?,
    modifier: Modifier,
    content: @Composable () -> Unit,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { bottomBar?.invoke() },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            RequestDetailsTopBar(onBackClick = onBack, titleRes = titleRes, modifier = Modifier.padding(horizontal = 12.dp))
            PullToRefreshBox(isRefreshing, onRefresh, Modifier.fillMaxSize()) {
                Column(
                    Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp),
                ) {
                    content()
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun CommonDetailSections(
    prescriptionUrl: String?,
    customerNotes: String?,
    paymentMethod: com.medsy.domain.orders.model.PaymentMethod,
    total: Double,
    totalLabelRes: Int,
) {
    var imageDialog by remember { mutableStateOf<String?>(null) }
    PrescriptionImageSection(
        imageUrl = prescriptionUrl,
        onImageClick = { prescriptionUrl?.let { imageDialog = it } },
        modifier = Modifier.padding(top = 24.dp),
    )
    imageDialog?.let { ZoomableImageDialog(it) { imageDialog = null } }
    customerNotes?.takeIf(String::isNotBlank)?.let {
        CustomerNotesSection(it, Modifier.padding(top = 24.dp))
    }
    PaymentMethodSection(paymentMethod, Modifier.padding(top = 24.dp))
    RequestTotalSummaryRow(total, Modifier.padding(top = 24.dp, bottom = 8.dp), totalLabelRes)
}

@Composable
private fun String.requestAgeLabel(): String? {
    if (!contains('T')) return null
    val minutes = runCatching {
        val normalized = if (endsWith("Z")) this else "${this}Z"
        Duration.between(Instant.parse(normalized), Instant.now()).toMinutes().coerceAtLeast(0).toInt()
    }.getOrNull() ?: return null
    return if (minutes >= 60) {
        stringResource(R.string.request_details_hours_minutes_ago, minutes / 60, minutes % 60)
    } else stringResource(R.string.request_details_minutes_ago, minutes)
}

@Composable
private fun String?.orderDateLabel(): String? {
    val raw = this?.takeIf(String::isNotBlank) ?: return null
    val locale = LocalConfiguration.current.locales[0]
    return runCatching {
        LocalDate.parse(raw.substringBefore('T')).format(
            DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)
        )
    }.getOrElse { raw }
}
