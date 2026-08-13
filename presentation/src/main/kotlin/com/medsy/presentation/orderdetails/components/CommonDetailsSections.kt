package com.medsy.presentation.orderdetails.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.medsy.domain.orders.model.PaymentMethod

@Composable
internal fun CommonDetailsSections(
    prescriptionUrl: String?,
    customerNotes: String?,
    paymentMethod: PaymentMethod,
    total: Double,
    @StringRes totalLabelRes: Int,
) {
    var imageDialogUrl by remember { mutableStateOf<String?>(null) }

    PrescriptionImageSection(
        imageUrl = prescriptionUrl,
        onImageClick = { prescriptionUrl?.let { imageDialogUrl = it } },
        modifier = Modifier.padding(top = 24.dp),
    )
    imageDialogUrl?.let { imageUrl ->
        ZoomableImageDialog(imageUrl) { imageDialogUrl = null }
    }
    customerNotes?.takeIf(String::isNotBlank)?.let { notes ->
        CustomerNotesSection(notes, Modifier.padding(top = 24.dp))
    }
    PaymentMethodSection(paymentMethod, Modifier.padding(top = 24.dp))
    RequestTotalSummaryRow(
        total = total,
        modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
        labelRes = totalLabelRes,
    )
}
