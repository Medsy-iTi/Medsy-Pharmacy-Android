package com.medsy.presentation.orderdetails.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medsy.presentation.R
import com.medsy.presentation.orderdetails.model.RequestMedicineItem

@Composable
fun RequestedMedicinesSection(
    items: List<RequestMedicineItem>,
    selectedItems: Set<Long>,
    onItemCheckedChange: (Long) -> Unit,
    onAddSubstituteClick: (Long) -> Unit,
    readOnly: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.request_details_requested_medicines),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
            ),
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                items.forEachIndexed { index, item ->
                    val itemIdLong = item.id.toLongOrNull() ?: -1L
                    MedicineRequestItemRow(
                        item = item,
                        isChecked = selectedItems.contains(itemIdLong),
                        onCheckedChange = if (readOnly) null else { { _: Boolean -> onItemCheckedChange(itemIdLong) } },
                        onAddSubstituteClick = if (readOnly) null else { { onAddSubstituteClick(itemIdLong) } },
                    )
                    if (index != items.lastIndex) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        )
                    }
                }
            }
        }
    }
}
