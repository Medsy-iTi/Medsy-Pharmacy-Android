package com.medsy.presentation.auth.approval

import androidx.annotation.StringRes
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.medsy.designsystem.components.MedsyButton
import com.medsy.designsystem.ui.theme.MedsyTheme
import com.medsy.presentation.R
import com.medsy.designsystem.R as DesignR

@Composable
fun ApprovalRoot(
    status: ApprovalScreenStatus,
    openLogin: () -> Unit,
) {
    ApprovalScreen(
        state = status.toApprovalState(),
        openLogin = openLogin,
    )
}

@Composable
private fun ApprovalScreen(
    state: ApprovalUiState,
    openLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
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
                modifier = Modifier.width(104.dp),
                contentScale = ContentScale.FillWidth,
            )

            Spacer(modifier = Modifier.height(18.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 520.dp),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp,
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    VerificationAnimation()

                    ApprovalStatusPill(
                        icon = state.statusIcon,
                        label = stringResource(state.statusLabel),
                    )

                    Text(
                        text = stringResource(state.title),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                        ),
                        textAlign = TextAlign.Center,
                    )

                    Text(
                        text = stringResource(state.supporting),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                        textAlign = TextAlign.Center,
                    )

                    ApprovalInfoCard(
                        icon = Icons.Filled.Description,
                        title = stringResource(R.string.approval_documents_title),
                        body = stringResource(state.documentsBody),
                    )

                    ApprovalInfoCard(
                        icon = state.guidanceIcon,
                        title = stringResource(state.guidanceTitle),
                        body = stringResource(state.guidanceBody),
                    )

                    MedsyButton(onClick = openLogin) {
                        Text(stringResource(R.string.approval_back_to_login))
                    }
                }
            }
        }
    }
}

@Composable
private fun VerificationAnimation() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.verified))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1,
    )
    val animationDescription = stringResource(R.string.approval_verification_animation_desc)

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = Modifier
            .size(180.dp)
            .semantics { contentDescription = animationDescription },
    )
}

@Composable
private fun ApprovalStatusPill(
    icon: ImageVector,
    label: String,
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
            )
        }
    }
}

@Composable
private fun ApprovalInfoCard(
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
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier.size(36.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp),
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                )
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

private data class ApprovalUiState(
    @StringRes val title: Int,
    @StringRes val supporting: Int,
    @StringRes val statusLabel: Int,
    val statusIcon: ImageVector,
    @StringRes val documentsBody: Int,
    @StringRes val guidanceTitle: Int,
    @StringRes val guidanceBody: Int,
    val guidanceIcon: ImageVector,
)

private fun ApprovalScreenStatus.toApprovalState(): ApprovalUiState = when (this) {
    ApprovalScreenStatus.Pending -> ApprovalUiState(
        title = R.string.approval_pending_title,
        supporting = R.string.approval_pending_supporting,
        statusLabel = R.string.approval_pending_status,
        statusIcon = Icons.Filled.HourglassTop,
        documentsBody = R.string.approval_pending_documents_body,
        guidanceTitle = R.string.approval_pending_guidance_title,
        guidanceBody = R.string.approval_pending_guidance_body,
        guidanceIcon = Icons.Filled.Verified,
    )

    ApprovalScreenStatus.Rejected -> ApprovalUiState(
        title = R.string.approval_rejected_title,
        supporting = R.string.approval_rejected_supporting,
        statusLabel = R.string.approval_rejected_status,
        statusIcon = Icons.Filled.Block,
        documentsBody = R.string.approval_rejected_documents_body,
        guidanceTitle = R.string.approval_support_guidance_title,
        guidanceBody = R.string.approval_rejected_guidance_body,
        guidanceIcon = Icons.Filled.SupportAgent,
    )

    ApprovalScreenStatus.Suspended -> ApprovalUiState(
        title = R.string.approval_suspended_title,
        supporting = R.string.approval_suspended_supporting,
        statusLabel = R.string.approval_suspended_status,
        statusIcon = Icons.Filled.AdminPanelSettings,
        documentsBody = R.string.approval_suspended_documents_body,
        guidanceTitle = R.string.approval_support_guidance_title,
        guidanceBody = R.string.approval_suspended_guidance_body,
        guidanceIcon = Icons.Filled.SupportAgent,
    )
}

enum class ApprovalScreenStatus {
    Pending,
    Rejected,
    Suspended,
}

@Preview(showBackground = true)
@Composable
private fun PendingApprovalPreview() {
    MedsyTheme {
        ApprovalScreen(
            state = ApprovalScreenStatus.Pending.toApprovalState(),
            openLogin = {},
        )
    }
}
