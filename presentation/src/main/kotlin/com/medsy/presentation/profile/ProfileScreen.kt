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
import com.medsy.presentation.profile.components.PharmacistHeaderCard
import com.medsy.presentation.profile.components.PharmacyInfoCard
import com.medsy.presentation.profile.components.ProfileItemRow
import com.medsy.presentation.profile.components.ProfileSettingsSection
import com.medsy.presentation.profile.components.ThemeBottomSheet

@Composable
fun ProfileRoot(
    openLogin: () -> Unit,
    openInvitePharmacist: () -> Unit,
    openPharmacistsList: () -> Unit,
    openPersonalInfo: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    LaunchedEffect(Unit) {
        viewModel.onIntent(ProfileUIIntent.Refresh)
    }
    
    LaunchedEffect(viewModel) {
        viewModel.effect.collect {
            when (it) {
                ProfileUIEffect.OpenLogin -> openLogin()
                ProfileUIEffect.OpenInvitePharmacist -> openInvitePharmacist()
                ProfileUIEffect.OpenPharmacistsList -> openPharmacistsList()
                ProfileUIEffect.OpenPersonalInfo -> openPersonalInfo()
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
          
            PharmacistHeaderCard(pharmacist = state.pharmacist)

            Spacer(modifier = Modifier.height(16.dp))

            PharmacyInfoCard(
                pharmacyName = state.pharmacy?.name ?: "",
                pharmacyAddress = state.pharmacy?.address
            )

            Spacer(modifier = Modifier.height(24.dp))

            ProfileSettingsSection(
                state = state,
                onIntent = onIntent,
                onShowLanguageSheet = { showLanguageSheet = true },
                onShowThemeSheet = { showThemeSheet = true }
            )
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
