package com.medsy.presentation.auth.nopharmacy.invitation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MarkEmailUnread
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.medsy.presentation.R

@Composable
fun InvitationInboxEmptyState(
    modifier: Modifier,
    errorRes: Int?,
    onRetry: () -> Unit,
) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.MarkEmailUnread,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = stringResource(
                if (errorRes == null) {
                    R.string.no_pharmacy_invitations_empty
                } else {
                    errorRes
                },
            ),
            modifier = Modifier.padding(top = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = if (errorRes == null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
        )
        if (errorRes != null) {
            OutlinedButton(
                onClick = onRetry,
                modifier = Modifier.padding(top = 16.dp),
            ) {
                Text(stringResource(R.string.no_pharmacy_retry_invitations))
            }
        }
    }
}
