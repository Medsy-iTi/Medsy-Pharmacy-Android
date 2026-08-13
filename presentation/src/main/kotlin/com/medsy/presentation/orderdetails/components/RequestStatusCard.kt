package com.medsy.presentation.orderdetails.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medsy.domain.orders.model.PharmacyRequestAssignmentStatus
import com.medsy.domain.orders.model.PharmacyRequestStatus
import com.medsy.presentation.R

@Composable
fun RequestStatusCard(
    requestStatus: PharmacyRequestStatus,
    assignmentStatus: PharmacyRequestAssignmentStatus,
    modifier: Modifier = Modifier,
) {
    val labelRes = when (requestStatus) {
        PharmacyRequestStatus.Completed -> R.string.request_details_request_completed
        PharmacyRequestStatus.Cancelled -> R.string.request_details_request_cancelled
        PharmacyRequestStatus.Expired -> R.string.request_details_request_expired
        PharmacyRequestStatus.Unknown -> R.string.request_details_request_unavailable
        PharmacyRequestStatus.Searching -> when (assignmentStatus) {
            PharmacyRequestAssignmentStatus.Pending -> return
            PharmacyRequestAssignmentStatus.OfferCreated -> R.string.request_details_offer_already_submitted
            PharmacyRequestAssignmentStatus.Expired -> R.string.request_details_request_expired
            PharmacyRequestAssignmentStatus.Unknown -> R.string.request_details_request_unavailable
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
    ) {
        Text(
            text = stringResource(labelRes),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
        )
    }
}
