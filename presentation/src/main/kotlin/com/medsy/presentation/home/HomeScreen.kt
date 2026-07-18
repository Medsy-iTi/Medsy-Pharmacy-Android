package com.medsy.presentation.home

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.ui.theme.MedsyTheme
import com.medsy.presentation.R
import com.medsy.presentation.common.PlaceholderScaffold

@Composable
fun HomeRoot() {
    HomeScreen()
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
) {
    var receiving by remember { mutableStateOf(false) }
    PlaceholderScaffold(
        title = stringResource(R.string.home_title),
        supportingText = stringResource(R.string.home_supporting),
        modifier = modifier,
    ) {
        Card {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = stringResource(R.string.home_receiving_status),
                    style = MaterialTheme.typography.titleMedium,
                )
                Switch(
                    checked = receiving,
                    onCheckedChange = { receiving = it },
                )
                Text(stringResource(R.string.home_receiving_placeholder))
            }
        }
        DashboardCard(R.string.home_incoming_requests, R.string.home_incoming_requests_value)
        DashboardCard(R.string.home_offers_sent, R.string.home_offers_sent_value)
        DashboardCard(R.string.home_offers_selected, R.string.home_offers_selected_value)
        DashboardCard(R.string.home_active_orders, R.string.home_active_orders_value)
        DashboardCard(R.string.home_completed_orders, R.string.home_completed_orders_value)
        DashboardCard(R.string.home_notifications, R.string.home_notifications_value)
    }
}

@Composable
private fun DashboardCard(
    @StringRes title: Int,
    @StringRes value: Int,
) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(stringResource(title), style = MaterialTheme.typography.titleMedium)
            Text(stringResource(value), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomePreview() {
    MedsyTheme {
        HomeScreen()
    }
}
