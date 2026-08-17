package com.medsy.presentation.profile.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.PeopleOutline
import androidx.compose.material.icons.outlined.PersonAddAlt
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medsy.domain.common.preferences.model.ThemeMode
import com.medsy.presentation.R
import com.medsy.presentation.profile.ProfileState
import com.medsy.presentation.profile.ProfileUIIntent

@Composable
fun ProfileSettingsSection(
    state: ProfileState,
    onIntent: (ProfileUIIntent) -> Unit,
    onShowLanguageSheet: () -> Unit,
    onShowThemeSheet: () -> Unit,
    modifier: Modifier = Modifier
) {

    val cardBorder = BorderStroke(
        width = 1.dp,
        color = MaterialTheme.colorScheme.outline
    )

    Card(
        modifier = modifier.fillMaxWidth(),
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
                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
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
                            enabled = !state.isPresenceSwitchLoading,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = MaterialTheme.colorScheme.primary,
                                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                                uncheckedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )
                    }
                }
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Notifications switch
            ProfileItemRow(
                icon = Icons.Default.Notifications,
                title = stringResource(R.string.profile_receiving_notifications),
                subtitle = null,
                showChevron = false,
                trailing = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (state.isReceivingNotifications) {
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = stringResource(
                                    if (state.isReceivingNotifications) R.string.profile_notifications_on
                                    else R.string.profile_notifications_off
                                ),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (state.isReceivingNotifications) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(
                            checked = state.isReceivingNotifications,
                            onCheckedChange = { onIntent(ProfileUIIntent.ReceivingNotificationsChanged(it)) },
                            enabled = !state.isNotificationSwitchLoading,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = MaterialTheme.colorScheme.primary,
                                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                                uncheckedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )
                    }
                }
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
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
                color = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Pharmacists in Pharmacy
            ProfileItemRow(
                icon = Icons.Outlined.PeopleOutline,
                title = stringResource(R.string.profile_pharmacists_in_pharmacy),
                subtitle = stringResource(R.string.profile_pharmacists_count, state.pharmacy?.pharmacists?.size ?: 0),
                onClick = { onIntent(ProfileUIIntent.NavigateToPharmacistsList) }
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
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
                color = MaterialTheme.colorScheme.outlineVariant,
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
                onClick = onShowLanguageSheet
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Appearance Item
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
                onClick = onShowThemeSheet
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Logout Item
            ProfileItemRow(
                icon = Icons.AutoMirrored.Outlined.ExitToApp,
                title = stringResource(R.string.profile_logout),
                subtitle = null,
                iconTint = MaterialTheme.colorScheme.error,
                titleColor = MaterialTheme.colorScheme.error,
                isLoading = state.isLoggingOut,
                onClick = { onIntent(ProfileUIIntent.ShowLogoutDialog) }
            )
        }
    }
}
