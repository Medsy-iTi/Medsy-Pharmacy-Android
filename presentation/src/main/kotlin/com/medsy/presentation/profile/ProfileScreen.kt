package com.medsy.presentation.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.PeopleOutline
import androidx.compose.material.icons.outlined.PersonAddAlt
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    openInvitePharmacist: () -> Unit,
    openPharmacistsList: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) {
        viewModel.effect.collect {
            when (it) {
                ProfileUIEffect.OpenLogin -> openLogin()
                ProfileUIEffect.OpenInvitePharmacist -> openInvitePharmacist()
                ProfileUIEffect.OpenPharmacistsList -> openPharmacistsList()
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
          
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_pharmacy),
                        contentDescription = null,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = state.pharmacist?.fullName ?: stringResource(R.string.profile_dummy_name),
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Filled.Verified,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.profile_verified_pharmacist),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(R.string.profile_experience_years, stringResource(R.string.profile_dummy_experience)),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            PharmacyInfoCard(
                pharmacyName = state.pharmacy?.name ?: stringResource(R.string.profile_dummy_pharmacy_name),
                rating = "4.8", // Keep static for now or extract to string resource
                ratingsCountRes = R.string.profile_dummy_ratings,
                verifiedTextRes = R.string.profile_verified_pharmacy,
                onClick = { /* Navigate to pharmacy details */ }
            )

            Spacer(modifier = Modifier.height(24.dp))

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
                        showChevron = false,
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

                    // Personal Info
                    ProfileItemRow(
                        icon = Icons.Outlined.PersonOutline,
                        title = stringResource(R.string.profile_my_personal_info),
                        subtitle = null,
                        onClick = { onIntent(ProfileUIIntent.NavigateToPersonalInfo) }
                    )

                    HorizontalDivider(
                        color = if (isDark) Color.White.copy(alpha = 0.15f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.08f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // Pharmacists in Pharmacy
                    ProfileItemRow(
                        icon = Icons.Outlined.PeopleOutline,
                        title = stringResource(R.string.profile_pharmacists_in_pharmacy),
                        subtitle = stringResource(R.string.profile_pharmacists_count, stringResource(R.string.profile_dummy_pharmacist_count)),
                        onClick = { onIntent(ProfileUIIntent.NavigateToPharmacistsList) }
                    )

                    HorizontalDivider(
                        color = if (isDark) Color.White.copy(alpha = 0.15f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.08f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // Invite Pharmacist
                    ProfileItemRow(
                        icon = Icons.Outlined.PersonAddAlt,
                        title = stringResource(R.string.profile_invite_pharmacist),
                        subtitle = null,
                        onClick = { onIntent(ProfileUIIntent.NavigateToInvitePharmacist) }
                    )

                    HorizontalDivider(
                        color = if (isDark) Color.White.copy(alpha = 0.15f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.08f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // Edit Profile
                    ProfileItemRow(
                        icon = Icons.Outlined.EditNote,
                        title = stringResource(R.string.profile_edit_profile),
                        subtitle = null,
                        onClick = { onIntent(ProfileUIIntent.NavigateToEditProfile) }
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
                        iconTint = MaterialTheme.colorScheme.error,
                        titleColor = MaterialTheme.colorScheme.error,
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
