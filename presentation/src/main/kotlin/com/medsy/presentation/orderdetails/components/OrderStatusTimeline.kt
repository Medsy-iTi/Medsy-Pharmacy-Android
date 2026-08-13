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
import com.medsy.domain.orders.model.OrderFulfillmentMethod
import com.medsy.domain.orders.model.PharmacyOrderStatus
import com.medsy.presentation.R

@Composable
fun OrderStatusTimeline(
    status: PharmacyOrderStatus,
    fulfillmentMethod: OrderFulfillmentMethod,
    modifier: Modifier = Modifier,
) {
    val timeline = status.toTimelineModel(fulfillmentMethod)
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                stringResource(timeline.titleRes),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (timeline.isError) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                },
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
            )
            PharmacyOrderStatusStepper(timeline.steps())
        }
    }
}

@Composable
private fun PharmacyOrderStatusStepper(steps: List<TimelineStep>) {
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
                    val lineColor = when (steps[destinationIndex].state) {
                        TimelineStepState.Completed -> successColor
                        TimelineStepState.Active -> activeColor
                        TimelineStepState.Error -> errorColor
                        TimelineStepState.Upcoming -> connectorColor
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
            steps.forEach { step ->
                val color = when (step.state) {
                    TimelineStepState.Completed -> successColor
                    TimelineStepState.Active -> activeColor
                    TimelineStepState.Error -> errorColor
                    TimelineStepState.Upcoming -> futureColor
                }
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier.size(28.dp).clip(CircleShape)
                            .background(
                                when (step.state) {
                                    TimelineStepState.Completed -> MaterialTheme.extendedColors.greenContainer
                                    TimelineStepState.Active -> MaterialTheme.extendedColors.orangeContainer
                                    TimelineStepState.Error -> MaterialTheme.extendedColors.redContainer
                                    TimelineStepState.Upcoming -> MaterialTheme.colorScheme.surface
                                },
                            )
                            .border(2.dp, color, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = step.icon,
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(15.dp),
                        )
                    }
                    Text(
                        stringResource(step.labelRes),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (step.state == TimelineStepState.Upcoming) {
                            FontWeight.Medium
                        } else {
                            FontWeight.Bold
                        },
                        color = color,
                        modifier = Modifier.padding(top = 8.dp),
                        maxLines = 2,
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
    val state: TimelineStepState,
)

private enum class TimelineStepState {
    Completed,
    Active,
    Upcoming,
    Error,
}

private enum class TimelinePhase(
    val labelRes: Int,
    val icon: ImageVector,
) {
    Preparing(R.string.orders_timeline_preparing, Icons.Outlined.Inventory2),
    Ready(R.string.orders_timeline_ready, Icons.Outlined.Inventory2),
    OnTheWay(R.string.orders_timeline_on_the_way, Icons.Outlined.LocalShipping),
    Delivered(R.string.orders_timeline_delivered, Icons.Outlined.TaskAlt),
    WaitingForCustomer(R.string.orders_timeline_waiting_for_customer, Icons.Outlined.Storefront),
    Collected(R.string.orders_timeline_collected, Icons.Outlined.TaskAlt),
}

private val deliveryPhases = listOf(
    TimelinePhase.Preparing,
    TimelinePhase.Ready,
    TimelinePhase.OnTheWay,
    TimelinePhase.Delivered,
)

private val pickupPhases = listOf(
    TimelinePhase.Preparing,
    TimelinePhase.Ready,
    TimelinePhase.WaitingForCustomer,
    TimelinePhase.Collected,
)

private sealed interface OrderTimelineModel {
    val titleRes: Int
    val isError: Boolean

    data class Progress(
        override val titleRes: Int,
        val phases: List<TimelinePhase>,
        val currentPhase: TimelinePhase,
        val isComplete: Boolean = false,
    ) : OrderTimelineModel {
        override val isError: Boolean = false
    }

    data class Inactive(
        override val titleRes: Int,
        val phases: List<TimelinePhase>,
    ) : OrderTimelineModel {
        override val isError: Boolean = false
    }

    data object Cancelled : OrderTimelineModel {
        override val titleRes: Int = R.string.orders_status_cancelled
        override val isError: Boolean = true
    }
}

private fun PharmacyOrderStatus.toTimelineModel(
    fulfillmentMethod: OrderFulfillmentMethod,
): OrderTimelineModel {
    val phases = fulfillmentMethod.timelinePhases()
    return when (this) {
        PharmacyOrderStatus.Pending -> OrderTimelineModel.Progress(
            titleRes = R.string.orders_status_pending,
            phases = phases,
            currentPhase = TimelinePhase.Preparing,
        )
        PharmacyOrderStatus.PendingPayment -> OrderTimelineModel.Progress(
            titleRes = R.string.orders_status_pending_payment,
            phases = phases,
            currentPhase = TimelinePhase.Preparing,
        )
        PharmacyOrderStatus.Preparing -> OrderTimelineModel.Progress(
            titleRes = R.string.orders_status_preparing,
            phases = phases,
            currentPhase = TimelinePhase.Preparing,
        )
        PharmacyOrderStatus.ReadyForPickup -> if (fulfillmentMethod == OrderFulfillmentMethod.Pickup) {
            OrderTimelineModel.Progress(
                titleRes = R.string.orders_status_ready_for_pickup,
                phases = phases,
                currentPhase = TimelinePhase.WaitingForCustomer,
            )
        } else {
            OrderTimelineModel.Inactive(R.string.orders_status_ready_for_pickup, phases)
        }
        PharmacyOrderStatus.ReadyForDelivery -> if (fulfillmentMethod == OrderFulfillmentMethod.Delivery) {
            OrderTimelineModel.Progress(
                titleRes = R.string.orders_status_ready_for_delivery,
                phases = phases,
                currentPhase = TimelinePhase.Ready,
            )
        } else {
            OrderTimelineModel.Inactive(R.string.orders_status_ready_for_delivery, phases)
        }
        PharmacyOrderStatus.OutForDelivery -> if (fulfillmentMethod == OrderFulfillmentMethod.Delivery) {
            OrderTimelineModel.Progress(
                titleRes = R.string.orders_status_out_for_delivery,
                phases = phases,
                currentPhase = TimelinePhase.OnTheWay,
            )
        } else {
            OrderTimelineModel.Inactive(R.string.orders_status_out_for_delivery, phases)
        }
        PharmacyOrderStatus.Delivered -> OrderTimelineModel.Progress(
            titleRes = R.string.orders_status_delivered,
            phases = phases,
            currentPhase = phases.last(),
            isComplete = true,
        )
        PharmacyOrderStatus.Cancelled -> OrderTimelineModel.Cancelled
        PharmacyOrderStatus.Unknown -> OrderTimelineModel.Inactive(
            titleRes = R.string.orders_status_unknown,
            phases = phases,
        )
    }
}

private fun OrderFulfillmentMethod.timelinePhases(): List<TimelinePhase> = when (this) {
    OrderFulfillmentMethod.Pickup -> pickupPhases
    OrderFulfillmentMethod.Delivery,
    OrderFulfillmentMethod.Unknown -> deliveryPhases
}

private fun OrderTimelineModel.steps(): List<TimelineStep> = when (this) {
    OrderTimelineModel.Cancelled -> listOf(
        TimelineStep(
            labelRes = R.string.orders_status_cancelled,
            icon = Icons.Default.Close,
            state = TimelineStepState.Error,
        ),
    )
    is OrderTimelineModel.Inactive -> phases.map { phase ->
        phase.toTimelineStep(TimelineStepState.Upcoming)
    }
    is OrderTimelineModel.Progress -> phases.map { phase ->
        val currentPhaseIndex = phases.indexOf(currentPhase)
        val phaseIndex = phases.indexOf(phase)
        val state = when {
            isComplete || phaseIndex < currentPhaseIndex -> TimelineStepState.Completed
            phase == currentPhase -> TimelineStepState.Active
            else -> TimelineStepState.Upcoming
        }
        phase.toTimelineStep(state)
    }
}

private fun TimelinePhase.toTimelineStep(
    state: TimelineStepState,
): TimelineStep = TimelineStep(
    labelRes = labelRes,
    icon = icon,
    state = state,
)
