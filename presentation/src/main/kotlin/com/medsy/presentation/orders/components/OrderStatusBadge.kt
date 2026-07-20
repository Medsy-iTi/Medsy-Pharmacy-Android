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
import com.medsy.presentation.orders.OrderStatus

@Composable
fun OrderStatusBadge(
    status: OrderStatus,
    modifier: Modifier = Modifier,
) {
    val (container: Color, content: Color, labelRes: Int) = when (status) {
        OrderStatus.New -> Triple(
            MaterialTheme.extendedColors.blueContainer,
            MaterialTheme.extendedColors.blueContent,
            R.string.order_details_new_badge,
        )

        OrderStatus.InProgress -> Triple(
            MaterialTheme.extendedColors.orangeContainer,
            MaterialTheme.extendedColors.orangeContent,
            R.string.orders_status_in_progress,
        )

        OrderStatus.Delivered -> Triple(
            MaterialTheme.extendedColors.neutralContainer,
            MaterialTheme.extendedColors.neutralContent,
            R.string.orders_filter_delivered,
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
