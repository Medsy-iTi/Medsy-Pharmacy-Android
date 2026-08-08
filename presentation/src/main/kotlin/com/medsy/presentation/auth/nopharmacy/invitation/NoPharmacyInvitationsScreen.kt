package com.medsy.presentation.auth.nopharmacy.invitation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.presentation.R
import com.medsy.presentation.auth.nopharmacy.invitation.components.InvitationCard
import com.medsy.presentation.auth.nopharmacy.invitation.components.InvitationInboxEmptyState

@Composable
fun NoPharmacyInvitationsRoot(
    navigateBack: () -> Unit,
    navigateHome: () -> Unit,
    viewModel: NoPharmacyInvitationsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                NoPharmacyInvitationsUIEffect.NavigateHome -> navigateHome()
            }
        }
    }

    NoPharmacyInvitationsScreen(
        state = state,
        onIntent = viewModel::onIntent,
        navigateBack = navigateBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoPharmacyInvitationsScreen(
    state: NoPharmacyInvitationsState,
    onIntent: (NoPharmacyInvitationsUIIntent) -> Unit,
    navigateBack: () -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.no_pharmacy_invitations_title)) },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.content_desc_back),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        when {
            state.isLoading -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }

            state.invitations.isEmpty() -> InvitationInboxEmptyState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                errorRes = state.errorRes,
                onRetry = { onIntent(NoPharmacyInvitationsUIIntent.Retry) },
            )

            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    Text(
                        text = stringResource(R.string.no_pharmacy_invitations_supporting),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                state.errorRes?.let { errorRes ->
                    item {
                        Text(
                            text = stringResource(errorRes),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
                items(state.invitations, key = { it.id }) { invitation ->
                    InvitationCard(
                        invitation = invitation,
                        isAccepting = state.acceptingInvitationId == invitation.id,
                        isAcceptEnabled = state.acceptingInvitationId == null,
                        onAccept = {
                            onIntent(NoPharmacyInvitationsUIIntent.AcceptInvitation(invitation.id))
                        },
                    )
                }
            }
        }
    }
}

