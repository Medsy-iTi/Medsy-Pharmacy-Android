package com.medsy.presentation.auth.nopharmacy

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MarkEmailUnread
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.medsy.designsystem.components.MedsyButton
import com.medsy.designsystem.ui.theme.MedsyTheme
import com.medsy.presentation.R

@Composable
fun NoPharmacyRoot(
    openPharmacyRegistration: () -> Unit,
    openLogin: () -> Unit,
    openInvitations: () -> Unit,
    viewModel: NoPharmacyViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                NoPharmacyUIEffect.NavigatePharmacyRegistration -> openPharmacyRegistration()
                NoPharmacyUIEffect.NavigateLogin -> openLogin()
                NoPharmacyUIEffect.NavigateInvitations -> openInvitations()
            }
        }
    }

    NoPharmacyScreen(state = state, onIntent = viewModel::onIntent)
}

@Composable
fun NoPharmacyScreen(
    state: NoPharmacyState,
    onIntent: (NoPharmacyUIIntent) -> Unit,
) {
    val animationContentDescription = stringResource(
        R.string.no_pharmacy_animation_content_desc,
    )
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.doctors))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
    )

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            )
            {
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ) {
                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .semantics {
                                contentDescription = animationContentDescription
                            },
                    )
                }

                Text(
                    text = stringResource(R.string.no_pharmacy_title),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                    ),
                )

                Text(
                    text = stringResource(R.string.no_pharmacy_supporting),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    ),
                )

                NoPharmacyGuidanceCard(
                    icon = Icons.Filled.Business,
                    title = stringResource(R.string.no_pharmacy_register_title),
                    body = stringResource(R.string.no_pharmacy_register_body),
                )

                when {
                    state.pendingInvitationCount > 0 -> PendingInvitationsCard(
                        count = state.pendingInvitationCount,
                        onClick = { onIntent(NoPharmacyUIIntent.OpenInvitations) },
                    )

                    else -> NoPharmacyGuidanceCard(
                        icon = Icons.Filled.Email,
                        title = stringResource(R.string.no_pharmacy_invite_title),
                        body = stringResource(R.string.no_pharmacy_invite_body),
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                MedsyButton(onClick = { onIntent(NoPharmacyUIIntent.RegisterPharmacy) }) {
                    Text(
                        text = stringResource(R.string.no_pharmacy_register_pharmacy),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
                        ),
                    )
                }

                OutlinedButton(
                    onClick = { onIntent(NoPharmacyUIIntent.SignOut) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Text(stringResource(R.string.no_pharmacy_back_to_login))
                }
            }
        }
    }
}

@Composable
private fun PendingInvitationsCard(
    count: Int,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.MarkEmailUnread,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = stringResource(R.string.no_pharmacy_pending_invitations_title),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    text = pluralStringResource(
                        R.plurals.no_pharmacy_pending_invitations_count,
                        count,
                        count,
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = stringResource(R.string.no_pharmacy_open_invitations),
            )
        }
    }
}

@Composable
private fun NoPharmacyGuidanceCard(
    icon: ImageVector,
    title: String,
    body: String,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier.size(32.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                )
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NoPharmacyPreview() {
    MedsyTheme {
        NoPharmacyScreen(state = NoPharmacyState(), onIntent = {})
    }
}
