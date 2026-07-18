package com.medsy.presentation.auth.login

import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.medsy.designsystem.ui.theme.MedsyTheme
import com.medsy.presentation.R
import com.medsy.presentation.common.PlaceholderScaffold

@Composable
fun LoginRoot(
    openHome: () -> Unit,
    openRegistration: () -> Unit,
) {
    LoginScreen(openHome = openHome, openRegistration = openRegistration)
}

@Composable
fun LoginScreen(
    openHome: () -> Unit,
    openRegistration: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    PlaceholderScaffold(
        title = stringResource(R.string.auth_login_title),
        supportingText = stringResource(R.string.auth_login_supporting),
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text(stringResource(R.string.auth_phone)) },
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.auth_password)) },
            visualTransformation = PasswordVisualTransformation(),
        )
        Button(onClick = openHome) {
            Text(stringResource(R.string.auth_login_action))
        }
        OutlinedButton(onClick = openRegistration) {
            Text(stringResource(R.string.auth_register_action))
        }
        Text(stringResource(R.string.auth_placeholder_notice))
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginPreview() {
    MedsyTheme {
        LoginScreen(openHome = {}, openRegistration = {})
    }
}
