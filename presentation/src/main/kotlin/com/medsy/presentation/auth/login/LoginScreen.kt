package com.medsy.presentation.auth.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.designsystem.components.MedsyButton
import com.medsy.designsystem.components.MedsySnackbarHost
import com.medsy.designsystem.components.showError
import com.medsy.presentation.R
import com.medsy.presentation.auth.login.components.DontHaveAccount
import com.medsy.presentation.auth.login.components.LoginEmailTextField
import com.medsy.presentation.auth.login.components.LoginPasswordInput
import com.medsy.presentation.auth.login.components.LoginSocialButton
import com.medsy.presentation.auth.login.components.OrDivider
import com.medsy.designsystem.R as DesignR

@Composable
fun LoginRoot(
    openRegistration: () -> Unit,
    openHome: () -> Unit,
    openNoPharmacy: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is LoginEffect.NavigateHome -> openHome()
                is LoginEffect.NavigateNoPharmacy -> openNoPharmacy()
                is LoginEffect.ShowError -> snackbarHostState.showError(
                    message = ContextCompat.getString(
                        context,
                        effect.messageRes
                    )
                )
            }
        }
    }

    LoginScreen(
        state = state,
        onIntent = viewModel::onIntent,
        openRegistration = openRegistration,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun LoginScreen(
    state: LoginState,
    onIntent: (LoginIntent) -> Unit,
    openRegistration: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val focusManager = LocalFocusManager.current

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            )
            {
                Image(
                    painter = painterResource(id = DesignR.drawable.ic_logo_transparent),
                    contentDescription = stringResource(R.string.medsy_logo_content_desc),
                    modifier = Modifier.size(90.dp),
                    contentScale = ContentScale.Fit,
                )
                Text(
                    text = stringResource(R.string.login_welcome_back),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                    ),
                )

                Text(
                    text = stringResource(R.string.login_motto),
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(32.dp))

                LoginEmailTextField(state.email, state.emailErrorRes, onIntent)

                LoginPasswordInput(
                    password = state.password,
                    onPasswordChange = { onIntent(LoginIntent.PasswordChanged(it)) },
                    errorRes = state.passwordErrorRes,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    Text(
                        text = stringResource(R.string.login_forgot_password),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                        ),
                        modifier = Modifier
                            .clickable { }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                MedsyButton(
                    onClick = {
                        focusManager.clearFocus()
                        onIntent(LoginIntent.Submit)
                    },
                    isLoading = state.isLoading,
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.auth_login_action),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary,
                            ),
                        )
                    }
                }

                OrDivider()

                LoginSocialButton(
                    iconResId = DesignR.drawable.ic_google,
                    text = stringResource(R.string.login_google),
                    enabled = !state.isLoading,
                    onClick = {
                        focusManager.clearFocus()
                        onIntent(LoginIntent.LoginWithGoogle)
                    },
                )

                DontHaveAccount(openRegistration)
            }
        }
    }

    MedsySnackbarHost(hostState = snackbarHostState)
}

@Preview
@Composable
fun PreviewLoginScreen() {
    LoginScreen(
        state = LoginState(),
        onIntent = {},
        openRegistration = {}
    )
}
