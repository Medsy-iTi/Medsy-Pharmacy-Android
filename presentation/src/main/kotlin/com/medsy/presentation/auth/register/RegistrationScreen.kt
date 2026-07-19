package com.medsy.presentation.auth.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.medsy.presentation.auth.register.components.AuthTextField
import com.medsy.presentation.auth.register.components.PasswordField
import com.medsy.presentation.auth.register.components.RegistrationStepper
import com.medsy.presentation.auth.register.components.ScreenHeader
import com.medsy.presentation.auth.register.components.SectionTitle
import com.medsy.presentation.auth.register.components.SignInFooter

@Composable
fun RegistrationRoot(
    onNavigateBack: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    onNavigateToOtp: (String) -> Unit,
    viewModel: RegisterViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is RegisterUIEffect.NavigateToOtp -> onNavigateToOtp(effect.email)
                is RegisterUIEffect.ShowError -> snackbarHostState.showError(
                    message = context.getString(effect.messageRes)
                )
            }
        }
    }

    RegistrationScreen(
        state = state,
        onIntent = viewModel::onIntent,
        onNavigateBack = onNavigateBack,
        onNavigateToSignIn = onNavigateToSignIn,
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun RegistrationScreen(
    state: RegisterUIState,
    onIntent: (RegisterUIIntent) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    var passwordVisible by remember { mutableStateOf(false) }

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

                RegistrationStepper(currentStep = state.currentStep)

                Spacer(modifier = Modifier.height(24.dp))

                SectionTitle(textRes = R.string.auth_section_personal_data)

                Spacer(modifier = Modifier.height(12.dp))

                AuthTextField(
                    value = state.firstName,
                    onValueChange = { onIntent(RegisterUIIntent.FirstNameChanged(it)) },
                    labelRes = R.string.auth_first_name,
                    leadingIcon = Icons.Filled.Person,
                    errorRes = state.firstNameErrorRes,
                )

                Spacer(modifier = Modifier.height(2.dp))

                AuthTextField(
                    value = state.lastName,
                    onValueChange = { onIntent(RegisterUIIntent.LastNameChanged(it)) },
                    labelRes = R.string.auth_last_name,
                    leadingIcon = Icons.Filled.Person,
                    errorRes = state.lastNameErrorRes,
                )

                Spacer(modifier = Modifier.height(2.dp))

                AuthTextField(
                    value = state.phoneNumber,
                    onValueChange = { onIntent(RegisterUIIntent.PhoneChanged(it)) },
                    labelRes = R.string.auth_phone,
                    leadingIcon = Icons.Filled.Phone,
                    errorRes = state.phoneErrorRes,
                    keyboardType = KeyboardType.Phone,
                )

                Spacer(modifier = Modifier.height(2.dp))

                AuthTextField(
                    value = state.email,
                    onValueChange = { onIntent(RegisterUIIntent.EmailChanged(it)) },
                    labelRes = R.string.auth_email,
                    leadingIcon = Icons.Filled.Email,
                    errorRes = state.emailErrorRes,
                    keyboardType = KeyboardType.Email,
                )

                Spacer(modifier = Modifier.height(2.dp))

                PasswordField(
                    value = state.password,
                    onValueChange = { onIntent(RegisterUIIntent.PasswordChanged(it)) },
                    labelRes = R.string.auth_password,
                    isVisible = passwordVisible,
                    onToggleVisibility = { passwordVisible = !passwordVisible },
                    errorRes = state.passwordErrorRes,
                )

                Spacer(modifier = Modifier.height(16.dp))

                MedsyButton(
                    onClick = { onIntent(RegisterUIIntent.Submit) },
                    isLoading = state.isLoading,
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.action_next),
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }

                SignInFooter(
                    onSignInClick = onNavigateToSignIn,
                    modifier = Modifier.padding(top = 8.dp, bottom = 24.dp),
                )
            }
        }
        MedsySnackbarHost(hostState = snackbarHostState)
    }
}
