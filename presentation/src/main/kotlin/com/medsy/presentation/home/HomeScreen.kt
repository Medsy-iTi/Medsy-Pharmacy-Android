package com.medsy.presentation.home

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
    val isDark = isSystemInDarkTheme()

    // White border in dark mode, subtle outline border in light mode
    val cardBorder = BorderStroke(
        width = 1.dp,
        color = if (isDark) Color.White.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
    )

    PlaceholderScaffold(
        title = stringResource(R.string.home_title),
        supportingText = stringResource(R.string.home_supporting),
        modifier = modifier,
    ) {
        // Active receiving status card - transparent background + border
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            border = cardBorder
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.home_receiving_status),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Switch(
                        checked = receiving,
                        onCheckedChange = { receiving = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            uncheckedThumbColor = if (isDark) Color.White else MaterialTheme.colorScheme.outline,
                            uncheckedTrackColor = if (isDark) Color.White.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant,
                            uncheckedBorderColor = if (isDark) Color.White.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline
                        )
                    )
                }
                Text(
                    text = stringResource(R.string.home_receiving_placeholder),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Dashboard items
        DashboardCard(R.string.home_incoming_requests, R.string.home_incoming_requests_value, cardBorder)
        DashboardCard(R.string.home_offers_sent, R.string.home_offers_sent_value, cardBorder)
        DashboardCard(R.string.home_offers_selected, R.string.home_offers_selected_value, cardBorder)
        DashboardCard(R.string.home_active_orders, R.string.home_active_orders_value, cardBorder)
        DashboardCard(R.string.home_completed_orders, R.string.home_completed_orders_value, cardBorder)
        DashboardCard(R.string.home_notifications, R.string.home_notifications_value, cardBorder)
    }
}

@Composable
private fun DashboardCard(
    @StringRes title: Int,
    @StringRes value: Int,
    border: BorderStroke
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = border
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = stringResource(title), 
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = stringResource(value), 
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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
