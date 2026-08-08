package com.medsy.presentation.auth.login.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.medsy.designsystem.components.MedsyTextField
import com.medsy.presentation.R
import com.medsy.presentation.auth.login.LoginUIIntent

@Composable
fun LoginEmailTextField(
    email: String,
    emailErrorRes: Int?,
    onIntent: (LoginUIIntent) -> Unit
) {
    MedsyTextField(
        value = email,
        onValueChange = { onIntent(LoginUIIntent.EmailChanged(it)) },
        placeholder = { Text(stringResource(R.string.auth_email)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Email,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
        ),
        singleLine = true,
        errorRes = emailErrorRes,
    )
}

