package com.medsy.presentation.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.designsystem.ui.theme.MedsyTheme
import com.medsy.domain.common.preferences.model.ThemeMode
import com.medsy.presentation.R
import com.medsy.presentation.profile.components.LanguageBottomSheet
import com.medsy.presentation.profile.components.PharmacyInfoCard
import com.medsy.presentation.profile.components.ProfileItemRow
import com.medsy.presentation.profile.components.ThemeBottomSheet

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
    var showLanguageSheet by remember { mutableStateOf(false) }
    var showThemeSheet by remember { mutableStateOf(false) }
    val isDark = isSystemInDarkTheme()

    // Transparent cards with high-contrast borders (white in dark theme)
    val cardBorder = BorderStroke(
        width = 1.dp,
        color = if (isDark) Color.White.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            // Note: Header "الملف الشخصي" and Settings Gear removed as requested.

            // ─── Pharmacy info card ──────────────────────────────────────────
            PharmacyInfoCard(
                pharmacyName = "صيدلية النهضية",
                rating = "4.8",
                ratingsCountRes = R.string.profile_ratings_count,
                verifiedTextRes = R.string.profile_verified_pharmacy,
                onClick = { /* View Pharmacy Details */ }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ─── Contact and Documents Card ──────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                border = cardBorder
            ) {
                Column {
                    // Phone Number Row (Outlined icon)
                    ProfileItemRow(
                        icon = Icons.Outlined.Phone,
                        title = stringResource(R.string.profile_phone_number),
                        subtitle = "010 1234 5678",
                        trailing = {
                            TextButton(onClick = { /* Change Action */ }) {
                                Text(
                                    text = stringResource(R.string.profile_change),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    )

                    HorizontalDivider(
                        color = if (isDark) Color.White.copy(alpha = 0.15f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.08f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // License Row (Outlined icon)
                    ProfileItemRow(
                        icon = Icons.Outlined.Security,
                        title = stringResource(R.string.profile_license),
                        subtitle = stringResource(R.string.profile_license_view),
                        onClick = { /* View License action */ }
                    )

                    HorizontalDivider(
                        color = if (isDark) Color.White.copy(alpha = 0.15f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.08f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // Location Row (Outlined icon)
                    ProfileItemRow(
                        icon = Icons.Outlined.Place,
                        title = stringResource(R.string.profile_registered_location),
                        subtitle = stringResource(R.string.profile_location_value),
                        onClick = { /* View Location action */ }
                    )

                    HorizontalDivider(
                        color = if (isDark) Color.White.copy(alpha = 0.15f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.08f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // Edit Request Row (Outlined icon)
                    ProfileItemRow(
                        icon = Icons.Outlined.EditNote,
                        title = stringResource(R.string.profile_edit_request),
                        subtitle = stringResource(R.string.profile_edit_request_sub),
                        onClick = { /* Request edit action */ }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ─── Settings & Logout Card ──────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                border = cardBorder
            ) {
                Column {
                    // Order status switch
                    ProfileItemRow(
                        icon = Icons.Outlined.Storefront,
                        title = stringResource(R.string.profile_receiving_status),
                        subtitle = null,
                        trailing = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                 modifier = Modifier
                                        .background(
                                            color = if (state.isReceivingOrders) {
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                            } else {
                                                if (isDark) Color.White.copy(alpha = 0.1f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                                            },
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = stringResource(
                                            if (state.isReceivingOrders) R.string.profile_status_open
                                            else R.string.profile_status_closed
                                        ),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (state.isReceivingOrders) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                            }
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Switch(
                                    checked = state.isReceivingOrders,
                                    onCheckedChange = { onIntent(ProfileUIIntent.ReceivingStatusChanged(it)) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                                        uncheckedThumbColor = if (isDark) Color.White else MaterialTheme.colorScheme.outline,
                                        uncheckedTrackColor = if (isDark) Color.White.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant,
                                        uncheckedBorderColor = if (isDark) Color.White.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline
                                    )
                                )
                            }
                        }
                    )

                    HorizontalDivider(
                        color = if (isDark) Color.White.copy(alpha = 0.15f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.08f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // Language Item
                    ProfileItemRow(
                        icon = Icons.Outlined.Language,
                        title = stringResource(R.string.profile_language),
                        subtitle = stringResource(
                            if (androidx.appcompat.app.AppCompatDelegate.getApplicationLocales().toLanguageTags().contains("ar")) {
                                R.string.profile_language_arabic
                            } else {
                                R.string.profile_language_english
                            }
                        ),
                        onClick = { showLanguageSheet = true }
                    )

                    HorizontalDivider(
                        color = if (isDark) Color.White.copy(alpha = 0.15f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.08f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // Appearance Item (Outlined icon)
                    ProfileItemRow(
                        icon = Icons.Outlined.DarkMode,
                        title = stringResource(R.string.profile_theme),
                        subtitle = stringResource(
                            when (state.themeMode) {
                                ThemeMode.System -> R.string.profile_theme_system
                                ThemeMode.Light -> R.string.profile_theme_light
                                ThemeMode.Dark -> R.string.profile_theme_dark
                            }
                        ),
                        onClick = { showThemeSheet = true }
                    )

                    HorizontalDivider(
                        color = if (isDark) Color.White.copy(alpha = 0.15f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.08f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // Logout Item
                    ProfileItemRow(
                        icon = Icons.AutoMirrored.Outlined.ExitToApp,
                        title = stringResource(R.string.profile_logout),
                        subtitle = null,
                        onClick = { onIntent(ProfileUIIntent.Logout) }
                    )
                }
            }
        }
    }

    // ─── Modern M3 Bottom Sheets ─────────────────────────────────────────────
    if (showLanguageSheet) {
        LanguageBottomSheet(
            onDismiss = { showLanguageSheet = false },
            onLanguageSelected = { tag ->
                onIntent(ProfileUIIntent.LanguageChanged(tag))
                showLanguageSheet = false
            }
        )
    }

    if (showThemeSheet) {
        ThemeBottomSheet(
            currentThemeMode = state.themeMode,
            onDismiss = { showThemeSheet = false },
            onThemeSelected = { mode ->
                onIntent(ProfileUIIntent.ThemeChanged(mode))
                showThemeSheet = false
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfilePreview() {
    MedsyTheme {
        ProfileScreen(state = ProfileState(), onIntent = {})
    }
}
