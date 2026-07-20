package com.medsy.presentation.orders.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medsy.presentation.R
import com.medsy.presentation.orders.OrderFilter

@Composable
fun OrdersFilterChipsRow(
    selectedFilter: OrderFilter,
    newCount: Int,
    inProgressCount: Int,
    onFilterSelected: (OrderFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    val chips = listOf(
        OrderFilter.All to stringResource(R.string.orders_filter_all),
        OrderFilter.New to stringResource(R.string.orders_filter_new_format, newCount),
        OrderFilter.InProgress to stringResource(
            R.string.orders_filter_in_progress_format,
            inProgressCount,
        ),
        OrderFilter.Delivered to stringResource(R.string.orders_filter_delivered),
    )

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp),
    ) {
        items(chips) { (filter, label) ->
            OrderFilterChip(
                label = label,
                selected = filter == selectedFilter,
                onClick = { onFilterSelected(filter) },
            )
        }
    }
}

@Composable
private fun OrderFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surface
    }
    val contentColor = if (selected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(containerColor)
            .then(
                if (selected) {
                    Modifier
                } else {
                    Modifier.border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(24.dp),
                    )
                },
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = contentColor,
        )
    }
}
