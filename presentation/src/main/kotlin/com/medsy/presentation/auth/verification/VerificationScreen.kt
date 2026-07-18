package com.medsy.presentation.auth.verification

import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.medsy.presentation.R
import com.medsy.presentation.common.PlaceholderScaffold

@Composable
fun VerificationRoot(
    openPendingApproval: () -> Unit,
) {
    var code by remember { mutableStateOf("") }
    PlaceholderScaffold(
        title = stringResource(R.string.verification_title),
        supportingText = stringResource(R.string.verification_supporting),
    ) {
        OutlinedTextField(
            value = code,
            onValueChange = { code = it },
            label = { Text(stringResource(R.string.verification_code)) },
        )
        Button(onClick = openPendingApproval) {
            Text(stringResource(R.string.verification_action))
        }
        Text(stringResource(R.string.auth_placeholder_notice))
    }
}
