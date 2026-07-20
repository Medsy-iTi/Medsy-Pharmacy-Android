package com.medsy.presentation.auth.login.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.medsy.presentation.R

@Composable
fun DontHaveAccount(openRegistration: () -> Unit) {
    val registerText = stringResource(R.string.auth_register_action)
    val fullText = stringResource(R.string.login_no_account_full, registerText)
    val annotated = buildAnnotatedString {
        val start = fullText.indexOf(registerText)

        append(fullText)

        addStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            ),
            start = start,
            end = start + registerText.length,
        )
    }

    Text(
        text = annotated,
        style = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.onBackground,
        ),
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .clickable { openRegistration() },
    )
}
