package com.medsy.presentation.orderdetails.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.medsy.designsystem.ui.theme.extendedColors
import com.medsy.presentation.R

@Composable
fun MedicineRequestItemRow(
    name: String,
    packInfo: String,
    quantity: Int,
    price: Double,
    imageUrl: String?,
    isChecked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    onAddSubstituteClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier.fillMaxWidth().clickable(enabled = onCheckedChange != null) {
            onCheckedChange?.invoke(!isChecked)
        }.padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onCheckedChange != null) Checkbox(isChecked, onCheckedChange, Modifier.padding(end = 8.dp))
        Box(
            Modifier.size(28.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.extendedColors.neutralContainer),
            contentAlignment = Alignment.Center,
        ) {
            Text(quantity.toString(), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        }
        Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
            Text(name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
            if (packInfo.isNotBlank()) Text(packInfo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(stringResource(R.string.request_details_price_egp, price), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            if (!isChecked && onAddSubstituteClick != null) {
                TextButton(onClick = onAddSubstituteClick) { Text(stringResource(R.string.request_details_add_alternative)) }
            }
        }
        Box(
            Modifier.size(56.dp).clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            imageUrl?.let {
                AsyncImage(model = it, contentDescription = name, modifier = Modifier.size(48.dp))
            }
        }
    }
}
