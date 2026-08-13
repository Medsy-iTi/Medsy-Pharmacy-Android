package com.medsy.presentation.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.PendingActions
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.ui.theme.extendedColors
import com.medsy.domain.dashboard.model.PharmacyDashboard
import com.medsy.presentation.R

@Composable
fun OverviewSection(dashboard: PharmacyDashboard) {
    Column {
        Text(
            stringResource(R.string.home_overview_title),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        Text(
            stringResource(R.string.home_dashboard_last_month),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            textAlign = TextAlign.Center,
        )
        EqualSizeStatisticsGrid {
            StatCard(
                title = stringResource(R.string.home_stat_requests_received),
                value = dashboard.requestsReceived.toString(),
                icon = Icons.Outlined.ShoppingBag,
                containerColor = MaterialTheme.extendedColors.blueContent
            )
            StatCard(
                title = stringResource(R.string.home_stat_offers_created),
                value = dashboard.offersCreated.toString(),
                icon = Icons.Outlined.PendingActions,
                containerColor = MaterialTheme.extendedColors.purpleContent
            )
            StatCard(
                title = stringResource(R.string.home_stat_total_orders),
                value = dashboard.totalOrders.toString(),
                icon = Icons.Outlined.LocalShipping,
                containerColor = MaterialTheme.extendedColors.prescriptionSuccessContent
            )
            StatCard(
                title = stringResource(R.string.home_stat_total_order_value),
                value = stringResource(R.string.home_stat_value_egp, dashboard.totalOrderValue),
                icon = Icons.Outlined.AccountBalanceWallet,
                containerColor = MaterialTheme.extendedColors.orangeContent
            )
        }
    }
}

@Composable
private fun EqualSizeStatisticsGrid(content: @Composable () -> Unit) {
    val horizontalSpacing = 12.dp
    val verticalSpacing = 12.dp
    Layout(
        content = content,
        modifier = Modifier.fillMaxWidth(),
    ) { measurables, constraints ->
        val horizontalSpacingPx = horizontalSpacing.roundToPx()
        val verticalSpacingPx = verticalSpacing.roundToPx()
        val cardWidth = (constraints.maxWidth - horizontalSpacingPx).coerceAtLeast(0) / 2
        val cardHeight = measurables.maxOfOrNull { it.minIntrinsicHeight(cardWidth) } ?: 0
        val cardConstraints = Constraints.fixed(cardWidth, cardHeight)
        val placeables = measurables.map { it.measure(cardConstraints) }
        val rowCount = (placeables.size + 1) / 2
        val gridHeight =
            (rowCount * cardHeight) + ((rowCount - 1).coerceAtLeast(0) * verticalSpacingPx)

        layout(constraints.maxWidth, gridHeight) {
            placeables.forEachIndexed { index, placeable ->
                val column = index % 2
                val row = index / 2
                placeable.placeRelative(
                    x = column * (cardWidth + horizontalSpacingPx),
                    y = row * (cardHeight + verticalSpacingPx),
                )
            }
        }
    }
}
