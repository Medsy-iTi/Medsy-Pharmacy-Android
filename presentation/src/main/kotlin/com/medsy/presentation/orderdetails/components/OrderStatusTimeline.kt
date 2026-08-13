package com.medsy.presentation.orderdetails.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.ui.theme.extendedColors
import com.medsy.domain.orders.model.PharmacyOrderStatus
import com.medsy.presentation.R

@Composable
fun OrderStatusTimeline(status: PharmacyOrderStatus, modifier: Modifier = Modifier) {
    val label = when (status) {
        PharmacyOrderStatus.Pending -> R.string.orders_status_pending
        PharmacyOrderStatus.PendingPayment -> R.string.orders_status_pending_payment
        PharmacyOrderStatus.Preparing -> R.string.orders_status_preparing
        PharmacyOrderStatus.ReadyForPickup -> R.string.orders_status_ready_for_pickup
        PharmacyOrderStatus.ReadyForDelivery -> R.string.orders_status_ready_for_delivery
        PharmacyOrderStatus.OutForDelivery -> R.string.orders_status_out_for_delivery
        PharmacyOrderStatus.Delivered -> R.string.orders_status_delivered
        PharmacyOrderStatus.Cancelled -> R.string.orders_status_cancelled
        PharmacyOrderStatus.Unknown -> R.string.orders_status_unknown
    }
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                stringResource(label),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (status == PharmacyOrderStatus.Cancelled) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                },
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
            )
            PharmacyOrderStatusStepper(status)
        }
    }
}

@Composable
private fun PharmacyOrderStatusStepper(status: PharmacyOrderStatus) {
    val cancelled = status == PharmacyOrderStatus.Cancelled
    val steps = if (cancelled) {
        listOf(
            TimelineStep(R.string.orders_timeline_placed, Icons.Outlined.ShoppingBag),
            TimelineStep(R.string.orders_status_cancelled, Icons.Default.Close),
        )
    } else {
        listOf(
            TimelineStep(R.string.orders_timeline_placed, Icons.Outlined.ShoppingBag),
            TimelineStep(R.string.orders_timeline_preparing, Icons.Outlined.Inventory2),
            TimelineStep(
                R.string.orders_timeline_ready,
                when (status) {
                    PharmacyOrderStatus.ReadyForPickup -> Icons.Outlined.Storefront
                    PharmacyOrderStatus.ReadyForDelivery,
                    PharmacyOrderStatus.OutForDelivery -> Icons.Outlined.LocalShipping
                    else -> Icons.Outlined.Inventory2
                },
            ),
            TimelineStep(R.string.orders_timeline_delivered, Icons.Outlined.TaskAlt),
        )
    }
    val currentStep = when (status) {
        PharmacyOrderStatus.Pending, PharmacyOrderStatus.PendingPayment -> 0
        PharmacyOrderStatus.Preparing -> 1
        PharmacyOrderStatus.ReadyForPickup,
        PharmacyOrderStatus.ReadyForDelivery,
        PharmacyOrderStatus.OutForDelivery -> 2
        PharmacyOrderStatus.Delivered -> 3
        PharmacyOrderStatus.Cancelled -> 1
        PharmacyOrderStatus.Unknown -> null
    }
    val successColor = MaterialTheme.extendedColors.success
    val activeColor = MaterialTheme.extendedColors.orangeContent
    val errorColor = MaterialTheme.colorScheme.error
    val futureColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
    val connectorColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    val layoutDirection = LocalLayoutDirection.current

    Box(Modifier.fillMaxWidth()) {
        Canvas(Modifier.fillMaxWidth().height(28.dp)) {
            if (steps.size > 1) {
                val centers = List(steps.size) { index ->
                    val logicalCenter = size.width * (index + 0.5f) / steps.size
                    if (layoutDirection == LayoutDirection.Ltr) logicalCenter else size.width - logicalCenter
                }
                for (index in 0 until centers.lastIndex) {
                    val destinationIndex = index + 1
                    val lineColor = when {
                        cancelled && destinationIndex == 1 -> errorColor
                        currentStep != null && destinationIndex <= currentStep -> successColor
                        currentStep != null && destinationIndex == currentStep + 1 -> activeColor
                        else -> connectorColor
                    }
                    drawLine(
                        color = lineColor,
                        start = androidx.compose.ui.geometry.Offset(centers[index], 14.dp.toPx()),
                        end = androidx.compose.ui.geometry.Offset(centers[index + 1], 14.dp.toPx()),
                        strokeWidth = 2.dp.toPx(),
                    )
                }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            steps.forEachIndexed { index, step ->
                val reached = currentStep != null && index <= currentStep
                val next = currentStep != null && index == currentStep + 1
                val cancelledStep = cancelled && index == 1
                val color = when {
                    cancelledStep -> MaterialTheme.colorScheme.error
                    reached -> successColor
                    next -> activeColor
                    else -> futureColor
                }
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier.size(28.dp).clip(CircleShape)
                            .background(
                                when {
                                    cancelledStep -> MaterialTheme.extendedColors.redContainer
                                    reached -> MaterialTheme.extendedColors.greenContainer
                                    next -> MaterialTheme.extendedColors.orangeContainer
                                    else -> MaterialTheme.colorScheme.surface
                                },
                            )
                            .border(2.dp, color, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = if (cancelledStep) Icons.Default.Close else step.icon,
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(15.dp),
                        )
                    }
                    Text(
                        stringResource(step.labelRes),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (reached || next) FontWeight.Bold else FontWeight.Medium,
                        color = color,
                        modifier = Modifier.padding(top = 8.dp),
                        maxLines = 1,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

private data class TimelineStep(
    val labelRes: Int,
    val icon: ImageVector,
)
