package com.medsy.presentation.auth.register.pharmacyinfo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.designsystem.components.MedsyButton
import com.medsy.designsystem.components.MedsySnackbarHost
import com.medsy.designsystem.components.showError
import com.medsy.presentation.R
import com.medsy.presentation.auth.register.RegisterUIEffect
import com.medsy.presentation.auth.register.RegisterUIIntent
import com.medsy.presentation.auth.register.RegisterUIState
import com.medsy.presentation.auth.register.RegisterViewModel
import com.medsy.presentation.auth.register.components.AuthTextField
import com.medsy.presentation.auth.register.components.RegistrationStepper
import com.medsy.presentation.auth.register.components.ScreenHeader
import com.medsy.presentation.auth.register.components.SectionTitle

@Composable
fun ProfessionalInfoRoot(
    onNavigateBack: () -> Unit,
    onNavigateToDocuments: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is RegisterUIEffect.NavigateToDocuments -> onNavigateToDocuments()

                is RegisterUIEffect.ShowError -> {
                    val message = context.resources.getString(effect.messageRes)
                    snackbarHostState.showError(message = context.getString(effect.messageRes))
                }

                else -> {}
            }
        }
    }

    ProfessionalInfoScreen(
        state = state,
        onIntent = viewModel::onIntent,
        onNavigateBack = onNavigateBack,
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun ProfessionalInfoScreen(
    state: RegisterUIState,
    onIntent: (RegisterUIIntent) -> Unit,
    onNavigateBack: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.Top,
            ) {
                ScreenHeader(
                    titleRes = R.string.auth_register_provider_title,
                    subtitleRes = R.string.auth_register_provider_subtitle,
                    onBackClick = onNavigateBack,
                    modifier = Modifier.padding(top = 8.dp),
                )

                Spacer(modifier = Modifier.height(20.dp))

                RegistrationStepper(currentStep = 2)

                Spacer(modifier = Modifier.height(24.dp))

                SectionTitle(textRes = R.string.auth_section_pharmacy_info)

                Spacer(modifier = Modifier.height(12.dp))

                AuthTextField(
                    value = state.pharmacyName,
                    onValueChange = { onIntent(RegisterUIIntent.PharmacyNameChanged(it)) },
                    labelRes = R.string.registration_pharmacy_name,
                    leadingIcon = Icons.Filled.Store,
                    errorRes = state.pharmacyNameErrorRes,
                )

                Spacer(modifier = Modifier.height(2.dp))

                AuthTextField(
                    value = state.licenseNumber,
                    onValueChange = { onIntent(RegisterUIIntent.LicenseNumberChanged(it)) },
                    labelRes = R.string.registration_license_number,
                    leadingIcon = Icons.Filled.Badge,
                    errorRes = state.licenseNumberErrorRes,
                )

                Spacer(modifier = Modifier.height(2.dp))

                AuthTextField(
                    value = state.pharmacyPhoneNumber,
                    onValueChange = { onIntent(RegisterUIIntent.PharmacyPhoneChanged(it)) },
                    labelRes = R.string.auth_phone,
                    leadingIcon = Icons.Filled.Phone,
                    errorRes = state.pharmacyPhoneErrorRes,
                    keyboardType = KeyboardType.Phone,
                )

                Spacer(modifier = Modifier.height(2.dp))

                AuthTextField(
                    value = state.pharmacyAddress,
                    onValueChange = { onIntent(RegisterUIIntent.PharmacyAddressChanged(it)) },
                    labelRes = R.string.registration_map_location,
                    leadingIcon = Icons.Filled.LocationOn,
                    errorRes = state.addressErrorRes,
                    trailingIcon = {
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                MedsyButton(
                    onClick = { onIntent(RegisterUIIntent.SubmitPharmacyInfo) },
                    isLoading = state.isLoading,
                ) {
                    Text(
                        text = stringResource(R.string.action_next),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        }
        MedsySnackbarHost(hostState = snackbarHostState)
    }
}
