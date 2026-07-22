package com.medsy.presentation.auth.registerpharmacy

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.LocalPharmacy
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.designsystem.components.MedsyButton
import com.medsy.designsystem.components.MedsySnackbarHost
import com.medsy.designsystem.components.MedsyTextField
import com.medsy.designsystem.components.showError
import com.medsy.designsystem.ui.theme.MedsyTheme
import com.medsy.presentation.R
import com.medsy.presentation.auth.registerpharmacy.components.LicensePickerCard
import com.medsy.presentation.auth.registerpharmacy.components.PharmacyLocationPickerCard
import com.medsy.designsystem.R as DesignR

@Composable
fun PharmacyRegistrationRoot(
    openPendingApproval: () -> Unit,
    viewModel: PharmacyRegistrationViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                PharmacyRegistrationEffect.NavigatePendingApproval -> openPendingApproval()
                is PharmacyRegistrationEffect.ShowError -> snackbarHostState.showError(
                    message = ContextCompat.getString(context, effect.messageRes),
                )
            }
        }
    }

    PharmacyRegistrationScreen(
        state = state,
        onIntent = viewModel::onIntent,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun PharmacyRegistrationScreen(
    modifier: Modifier = Modifier,
    state: PharmacyRegistrationState,
    onIntent: (PharmacyRegistrationIntent) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(id = DesignR.drawable.ic_logo_transparent),
                    contentDescription = stringResource(R.string.medsy_logo_content_desc),
                    modifier = Modifier.width(112.dp),
                    contentScale = ContentScale.FillWidth,
                )

                Text(
                    text = stringResource(R.string.pharmacy_registration_title),
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    ),
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.pharmacy_registration_supporting),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 420.dp),
                )

                Spacer(modifier = Modifier.height(12.dp))

                PharmacyRegistrationTip()

                Spacer(modifier = Modifier.height(24.dp))

                Column(

                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    MedsyTextField(
                        value = state.pharmacyName,
                        onValueChange = {
                            onIntent(PharmacyRegistrationIntent.PharmacyNameChanged(it))
                        },
                        placeholder = {
                            Text(stringResource(R.string.pharmacy_registration_pharmacy_name))
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Business,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        },
                        singleLine = true,
                        errorRes = state.pharmacyNameErrorRes,
                    )

                    MedsyTextField(
                        value = state.phoneNumber,
                        onValueChange = {
                            onIntent(PharmacyRegistrationIntent.PhoneNumberChanged(it))
                        },
                        placeholder = {
                            Text(stringResource(R.string.pharmacy_registration_phone))
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Phone,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                    )

                    MedsyTextField(
                        value = state.address,
                        onValueChange = {
                            onIntent(PharmacyRegistrationIntent.AddressChanged(it))
                        },
                        placeholder = {
                            Text(stringResource(R.string.pharmacy_registration_address))
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        },
                        singleLine = false,
                    )

                    PharmacyLocationPickerCard(
                        selectedLatitude = state.selectedLatitude,
                        selectedLongitude = state.selectedLongitude,
                        locationErrorRes = state.locationErrorRes,
                        onLocationSelected = { latitude, longitude ->
                            onIntent(
                                PharmacyRegistrationIntent.LocationSelected(
                                    latitude = latitude,
                                    longitude = longitude,
                                ),
                            )
                        },
                    )

                    LicensePickerCard(
                        licenseName = state.licenseName,
                        licenseSizeKb = state.licenseSizeKb,
                        licenseErrorRes = state.licenseErrorRes,
                        onLicenseSelected = { displayName, mimeType, bytes ->
                            onIntent(
                                PharmacyRegistrationIntent.LicenseSelected(
                                    displayName = displayName,
                                    mimeType = mimeType,
                                    bytes = bytes,
                                ),
                            )
                        },
                        onLicenseSelectionFailed = { messageRes ->
                            onIntent(
                                PharmacyRegistrationIntent.LicenseSelectionFailed(messageRes),
                            )
                        },
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    MedsyButton(
                        onClick = { onIntent(PharmacyRegistrationIntent.Submit) },
                        isLoading = state.isSubmitting,
                    ) {
                        if (state.isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.pharmacy_registration_submit),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                ),
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        MedsySnackbarHost(hostState = snackbarHostState)
    }
}

@Composable
private fun PharmacyRegistrationTip() {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.LocalPharmacy,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = stringResource(R.string.pharmacy_registration_badge),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PharmacyRegistrationPreview() {
    MedsyTheme {
        PharmacyRegistrationScreen(
            state = PharmacyRegistrationState(),
            onIntent = {},
        )
    }
}
