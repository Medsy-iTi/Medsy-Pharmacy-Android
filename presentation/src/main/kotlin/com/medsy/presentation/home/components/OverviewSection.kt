package com.medsy.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.ui.theme.extendedColors
import com.medsy.presentation.R
import com.medsy.presentation.home.HomeStatsUI

@Composable
fun OverviewSection(stats: HomeStatsUI) {
    Column {
        Text(
            text = stringResource(R.string.home_overview_title),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            textAlign = TextAlign.Center
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.home_stat_new_orders),
                value = stats.newOrders.toString(),
                icon = Icons.Outlined.ShoppingBag,
                containerColor = MaterialTheme.extendedColors.blueContent
            )
            StatCard(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.home_stat_in_progress),
                value = stats.inProgress.toString(),
                icon = Icons.Outlined.PendingActions,
                containerColor = MaterialTheme.extendedColors.purpleContent
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.home_stat_delivered_today),
                value = stats.deliveredToday.toString(),
                icon = Icons.Outlined.LocalShipping,
                containerColor = MaterialTheme.extendedColors.prescriptionSuccessContent
            )
            StatCard(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.home_stat_total_sales),
                value = stats.totalSales,
                icon = Icons.Outlined.AccountBalanceWallet,
                containerColor = MaterialTheme.extendedColors.orangeContent
            )
        }
    }
}
