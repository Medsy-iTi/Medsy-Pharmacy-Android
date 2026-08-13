package com.medsy.presentation.worklist.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.ui.theme.extendedColors
import com.medsy.domain.orders.model.PharmacyOrderStatus
import com.medsy.domain.orders.model.PharmacyRequestAssignmentStatus
import com.medsy.presentation.R

@Composable
fun RequestStatusBadge(
    status: PharmacyRequestAssignmentStatus,
    modifier: Modifier = Modifier,
) {
    val values = when (status) {
        PharmacyRequestAssignmentStatus.Pending -> StatusBadgeValues(
            MaterialTheme.extendedColors.blueContainer,
            MaterialTheme.extendedColors.blueContent,
            R.string.requests_status_pending,
        )
        PharmacyRequestAssignmentStatus.OfferCreated -> StatusBadgeValues(
            MaterialTheme.extendedColors.greenContainer,
            MaterialTheme.extendedColors.greenContent,
            R.string.requests_status_offer_created,
        )
        PharmacyRequestAssignmentStatus.Expired -> StatusBadgeValues(
            MaterialTheme.extendedColors.redContainer,
            MaterialTheme.extendedColors.redContent,
            R.string.requests_status_expired,
        )
        PharmacyRequestAssignmentStatus.Unknown -> StatusBadgeValues(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant,
            R.string.requests_status_unavailable,
        )
    }
    StatusBadge(values, modifier)
}

@Composable
fun OrderStatusBadge(
    status: PharmacyOrderStatus,
    modifier: Modifier = Modifier,
) {
    val values = when (status) {
        PharmacyOrderStatus.Pending,
        PharmacyOrderStatus.PendingPayment -> StatusBadgeValues(
            MaterialTheme.extendedColors.blueContainer,
            MaterialTheme.extendedColors.blueContent,
            if (status == PharmacyOrderStatus.PendingPayment) {
                R.string.orders_status_pending_payment
            } else R.string.orders_status_pending,
        )
        PharmacyOrderStatus.Preparing -> StatusBadgeValues(
            MaterialTheme.extendedColors.orangeContainer,
            MaterialTheme.extendedColors.orangeContent,
            R.string.orders_status_preparing,
        )
        PharmacyOrderStatus.ReadyForPickup -> StatusBadgeValues(
            MaterialTheme.extendedColors.greenContainer,
            MaterialTheme.extendedColors.greenContent,
            R.string.orders_status_ready_for_pickup,
        )
        PharmacyOrderStatus.ReadyForDelivery -> StatusBadgeValues(
            MaterialTheme.extendedColors.greenContainer,
            MaterialTheme.extendedColors.greenContent,
            R.string.orders_status_ready_for_delivery,
        )
        PharmacyOrderStatus.OutForDelivery -> StatusBadgeValues(
            MaterialTheme.extendedColors.blueContainer,
            MaterialTheme.extendedColors.blueContent,
            R.string.orders_status_out_for_delivery,
        )
        PharmacyOrderStatus.Delivered -> StatusBadgeValues(
            MaterialTheme.extendedColors.greenContainer,
            MaterialTheme.extendedColors.greenContent,
            R.string.orders_status_delivered,
        )
        PharmacyOrderStatus.Cancelled -> StatusBadgeValues(
            MaterialTheme.extendedColors.redContainer,
            MaterialTheme.extendedColors.redContent,
            R.string.orders_status_cancelled,
        )
        PharmacyOrderStatus.Unknown -> StatusBadgeValues(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant,
            R.string.orders_status_unknown,
        )
    }
    StatusBadge(values, modifier)
}

private data class StatusBadgeValues(
    val container: Color,
    val content: Color,
    val labelRes: Int,
)

@Composable
private fun StatusBadge(values: StatusBadgeValues, modifier: Modifier) {
    Box(
        modifier = modifier
            .background(values.container, RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Text(
            text = stringResource(values.labelRes),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = values.content,
        )
    }
}
