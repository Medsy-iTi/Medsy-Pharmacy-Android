package com.medsy.presentation.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.designsystem.ui.theme.MedsyTheme
import com.medsy.domain.common.preferences.model.ThemeMode
import com.medsy.presentation.R
import com.medsy.presentation.common.PlaceholderScaffold

@Composable
fun ProfileRoot(
    openLogin: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) {
        viewModel.effect.collect {
            when (it) {
                ProfileUIEffect.OpenLogin -> openLogin()
            }
        }
    }
    ProfileScreen(state = state, onIntent = viewModel::onIntent)
}

@Composable
fun ProfileScreen(
    state: ProfileState,
    onIntent: (ProfileUIIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    PlaceholderScaffold(
        title = stringResource(R.string.profile_title),
        supportingText = stringResource(R.string.profile_supporting),
        modifier = modifier,
    ) {
        Text(stringResource(R.string.profile_account_placeholder))
        Text(stringResource(R.string.profile_approval_placeholder))
        Text(stringResource(R.string.profile_receiving_placeholder))
        Text(stringResource(R.string.profile_theme))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ThemeMode.entries.forEach { mode ->
                FilterChip(
                    selected = state.themeMode == mode,
                    onClick = { onIntent(ProfileUIIntent.ThemeChanged(mode)) },
                    label = {
                        Text(
                            stringResource(
                                when (mode) {
                                    ThemeMode.System -> R.string.profile_theme_system
                                    ThemeMode.Light -> R.string.profile_theme_light
                                    ThemeMode.Dark -> R.string.profile_theme_dark
                                },
                            ),
                        )
                    },
                )
            }
        }
        Text(stringResource(R.string.profile_language))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { onIntent(ProfileUIIntent.LanguageChanged("ar")) }) {
                Text(stringResource(R.string.profile_language_arabic))
            }
            OutlinedButton(onClick = { onIntent(ProfileUIIntent.LanguageChanged("en")) }) {
                Text(stringResource(R.string.profile_language_english))
            }
        }
        OutlinedButton(onClick = { onIntent(ProfileUIIntent.Logout) }) {
            Text(stringResource(R.string.profile_logout))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfilePreview() {
    MedsyTheme {
        ProfileScreen(state = ProfileState(), onIntent = {})
    }
}
