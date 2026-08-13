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
import com.medsy.domain.orders.model.OrderItem
import com.medsy.domain.orders.model.RequestItem
import com.medsy.presentation.R
import com.medsy.presentation.orderdetails.SubstituteDraft

@Composable
fun RequestedMedicinesSection(
    items: List<RequestItem>,
    selectedItems: Set<Long>,
    substitutes: Map<Long, SubstituteDraft>,
    onItemCheckedChange: (Long) -> Unit,
    onAddSubstituteClick: (Long) -> Unit,
    readOnly: Boolean,
    modifier: Modifier = Modifier,
) {
    MedicinesCard(modifier) {
        items.forEachIndexed { index, item ->
            val substitute = substitutes[item.id]
            MedicineRequestItemRow(
                name = substitute?.productName ?: item.productName,
                packInfo = listOfNotNull(item.strength, item.packSize, item.form).filter(String::isNotBlank).joinToString(" • "),
                quantity = item.quantity,
                price = substitute?.productPrice ?: item.unitPrice,
                imageUrl = substitute?.productImage ?: item.imageUrl,
                isChecked = item.id in selectedItems,
                onCheckedChange = if (readOnly) null else { { onItemCheckedChange(item.id) } },
                onAddSubstituteClick = if (readOnly) null else { { onAddSubstituteClick(item.id) } },
            )
            if (index != items.lastIndex) HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        }
    }
}

@Composable
fun OrderedMedicinesSection(items: List<OrderItem>, modifier: Modifier = Modifier) {
    MedicinesCard(modifier) {
        items.forEachIndexed { index, item ->
            MedicineRequestItemRow(
                name = item.productName,
                packInfo = listOfNotNull(item.strength, item.packSize, item.form).filter(String::isNotBlank).joinToString(" • "),
                quantity = item.quantity,
                price = item.unitPrice,
                imageUrl = item.imageUrl,
                isChecked = false,
                onCheckedChange = null,
            )
            if (index != items.lastIndex) HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        }
    }
}

@Composable
private fun MedicinesCard(modifier: Modifier, content: @Composable () -> Unit) {
    Column(modifier.fillMaxWidth()) {
        Text(
            stringResource(R.string.request_details_requested_medicines),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Card(
            Modifier.fillMaxWidth(),
            RoundedCornerShape(16.dp),
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        ) { Column(Modifier.padding(horizontal = 16.dp)) { content() } }
    }
}
