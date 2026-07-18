package com.medsy.presentation.auth.register

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
import androidx.compose.ui.tooling.preview.Preview
import com.medsy.designsystem.ui.theme.MedsyTheme
import com.medsy.presentation.R
import com.medsy.presentation.common.PlaceholderScaffold

@Composable
fun RegistrationRoot(
    openVerification: () -> Unit,
) {
    RegistrationScreen(openVerification = openVerification)
}

@Composable
fun RegistrationScreen(
    openVerification: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var value by remember { mutableStateOf("") }
    PlaceholderScaffold(
        title = stringResource(R.string.registration_title),
        supportingText = stringResource(R.string.registration_supporting),
        modifier = modifier,
    ) {
        listOf(
            R.string.registration_pharmacy_name,
            R.string.registration_license_number,
            R.string.registration_phone,
            R.string.registration_contact_person,
            R.string.registration_password,
            R.string.registration_map_location,
            R.string.registration_documents,
        ).forEach { label ->
            OutlinedTextField(
                value = value,
                onValueChange = { value = it },
                label = { Text(stringResource(label)) },
                enabled = label !in listOf(
                    R.string.registration_map_location,
                    R.string.registration_documents,
                ),
            )
        }
        Button(onClick = openVerification) {
            Text(stringResource(R.string.registration_continue))
        }
        Text(stringResource(R.string.auth_placeholder_notice))
    }
}

@Preview(showBackground = true)
@Composable
private fun RegistrationPreview() {
    MedsyTheme {
        RegistrationScreen(openVerification = {})
    }
}
