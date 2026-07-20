package com.medsy.presentation.auth.register.documents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.designsystem.components.MedsyButton
import com.medsy.designsystem.components.MedsySnackbarHost
import com.medsy.presentation.R
import com.medsy.presentation.auth.register.RegisterUIIntent
import com.medsy.presentation.auth.register.RegisterUIState
import com.medsy.presentation.auth.register.RegisterViewModel
import com.medsy.presentation.auth.register.components.RegistrationStepper
import com.medsy.presentation.auth.register.components.ScreenHeader
import com.medsy.presentation.auth.register.components.SectionTitle

@Composable
fun DocumentsRoot(
    onNavigateBack: () -> Unit,
    onNavigateToReview: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    DocumentsScreen(
        state = state,
        onIntent = viewModel::onIntent,
        onNavigateBack = onNavigateBack,
        onNavigateToReview = onNavigateToReview,
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun DocumentsScreen(
    state: RegisterUIState,
    onIntent: (RegisterUIIntent) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToReview: () -> Unit,
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

                RegistrationStepper(currentStep = 3)

                Spacer(modifier = Modifier.height(24.dp))

                SectionTitle(textRes = R.string.auth_step_documents)

                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Upload pharmacy license and documents",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(32.dp))



                Spacer(modifier = Modifier.weight(1f))

                MedsyButton(
                    onClick = { onNavigateToReview() },
                    isLoading = state.isLoading,
                ) {
                    Text(
                        text = stringResource(R.string.action_next),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
        MedsySnackbarHost(hostState = snackbarHostState)
    }
}
