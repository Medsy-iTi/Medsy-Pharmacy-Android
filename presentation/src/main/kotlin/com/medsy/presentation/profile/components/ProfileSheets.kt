package com.medsy.presentation.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medsy.domain.common.preferences.model.ThemeMode
import com.medsy.presentation.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageBottomSheet(
    onDismiss: () -> Unit,
    onLanguageSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(),
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp, top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.profile_language),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            val isArabic = androidx.appcompat.app.AppCompatDelegate.getApplicationLocales().toLanguageTags().contains("ar")

            LanguageOptionRow(
                label = stringResource(R.string.profile_language_arabic),
                selected = isArabic,
                onClick = { onLanguageSelected("ar") }
            )

            LanguageOptionRow(
                label = stringResource(R.string.profile_language_english),
                selected = !isArabic,
                onClick = { onLanguageSelected("en") }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeBottomSheet(
    currentThemeMode: ThemeMode,
    onDismiss: () -> Unit,
    onThemeSelected: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(),
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp, top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.profile_theme),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            ThemeMode.entries.forEach { mode ->
                val isSelected = currentThemeMode == mode
                LanguageOptionRow(
                    label = stringResource(
                        when (mode) {
                            ThemeMode.System -> R.string.profile_theme_system
                            ThemeMode.Light -> R.string.profile_theme_light
                            ThemeMode.Dark -> R.string.profile_theme_dark
                        }
                    ),
                    selected = isSelected,
                    onClick = { onThemeSelected(mode) }
                )
            }
        }
    }
}
