package com.medsy.presentation.requests.components

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
import com.medsy.presentation.requests.PharmacyWorkStatus

@Composable
fun RequestStatusBadge(status: PharmacyWorkStatus, modifier: Modifier = Modifier) {
    val (container: Color, content: Color, labelRes: Int) = when (status) {
        PharmacyWorkStatus.Searching -> Triple(
            MaterialTheme.extendedColors.blueContainer,
            MaterialTheme.extendedColors.blueContent,
            R.string.request_details_new_badge
        )

        PharmacyWorkStatus.WaitingForCustomer -> Triple(
            MaterialTheme.extendedColors.blueContainer,
            MaterialTheme.extendedColors.blueContent,
            R.string.orders_status_active_offer
        )

        PharmacyWorkStatus.RejectedOffer -> Triple(
            MaterialTheme.extendedColors.redContainer,
            MaterialTheme.extendedColors.redContent,
            R.string.orders_status_rejected_offer
        )

        PharmacyWorkStatus.Preparing -> Triple(
            MaterialTheme.extendedColors.orangeContainer,
            MaterialTheme.extendedColors.orangeContent,
            R.string.orders_timeline_preparing
        )

        PharmacyWorkStatus.OnTheWay -> Triple(
            MaterialTheme.extendedColors.blueContainer,
            MaterialTheme.extendedColors.blueContent,
            R.string.orders_timeline_on_the_way
        )

        PharmacyWorkStatus.Delivered -> Triple(
            MaterialTheme.extendedColors.greenContainer,
            MaterialTheme.extendedColors.greenContent,
            R.string.orders_timeline_delivered
        )
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(container)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            stringResource(labelRes),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = content
        )
    }
}
