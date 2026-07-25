package com.medsy.presentation.orders.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.ui.theme.extendedColors
import com.medsy.presentation.R
import com.medsy.presentation.orders.RequestStatus

@Composable
fun RequestStatusBadge(
    status: RequestStatus,
    modifier: Modifier = Modifier,
) {
    val (container: Color, content: Color, labelRes: Int) = when (status) {
        RequestStatus.Searching,
        RequestStatus.New -> Triple(
            MaterialTheme.extendedColors.blueContainer,
            MaterialTheme.extendedColors.blueContent,
            R.string.request_details_new_badge,
        )

        RequestStatus.InProgress -> Triple(
            MaterialTheme.extendedColors.orangeContainer,
            MaterialTheme.extendedColors.orangeContent,
            R.string.requests_status_in_progress,
        )

        RequestStatus.Delivered -> Triple(
            MaterialTheme.extendedColors.neutralContainer,
            MaterialTheme.extendedColors.neutralContent,
            R.string.requests_filter_delivered,
        )
        RequestStatus.Cancelled -> Triple(
            MaterialTheme.extendedColors.redContainer,
            MaterialTheme.extendedColors.redContent,
            R.string.requests_filter_cancelled,
        )
        RequestStatus.Expired -> Triple(
            MaterialTheme.extendedColors.redContainer,
            MaterialTheme.extendedColors.redContent,
            R.string.requests_status_expired,
        )
        RequestStatus.Completed -> Triple(
            MaterialTheme.extendedColors.greenContainer,
            MaterialTheme.extendedColors.greenContent,
            R.string.requests_filter_completed,
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(container)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Text(
            text = stringResource(labelRes),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = content,
        )
    }
}
