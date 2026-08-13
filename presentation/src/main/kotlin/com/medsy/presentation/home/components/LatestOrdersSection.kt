package com.medsy.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.components.MedsyLottie
import com.medsy.domain.orders.model.PharmacyOrder
import com.medsy.presentation.R

@Composable
fun LatestOrdersSection(orders: List<PharmacyOrder>, onOrderClick: (Long) -> Unit) {
    Column {
        Text(stringResource(R.string.home_latest_orders_title), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        Spacer(Modifier.height(12.dp))
        if (orders.isEmpty()) {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                MedsyLottie(com.medsy.designsystem.R.raw.no_data_found, Modifier.height(150.dp))
                Text(stringResource(R.string.orders_empty), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                orders.forEach { order -> OrderListItem(order) { onOrderClick(order.id) } }
            }
        }
    }
}
