package com.medsy.presentation.aichat

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.designsystem.components.MedsySnackbarHost
import com.medsy.designsystem.components.showInfo
import com.medsy.domain.pharmacy.model.PharmacyPharmacist
import com.medsy.presentation.R
import com.medsy.presentation.aichat.components.AiChatComposer
import com.medsy.presentation.aichat.components.AiChatConversation
import com.medsy.presentation.aichat.components.AiChatNewChatDialog
import com.medsy.presentation.aichat.components.AiChatTopBar
import com.medsy.presentation.aichat.components.AiChatTypingIndicator
import com.medsy.presentation.aichat.components.AiChatWelcome
import kotlinx.coroutines.flow.collectLatest
import java.util.Locale

@Composable
fun AiChatRoot(
    onNavigateBack: () -> Unit,
    onDial: (String) -> Unit,
    viewModel: AiChatViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val context = LocalContext.current
    val voiceLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        val text = if (result.resultCode == Activity.RESULT_OK) {
            result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
        } else null
        viewModel.onIntent(AiChatUIIntent.VoiceResult(text))
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
        viewModel.onIntent(AiChatUIIntent.CameraCaptureCompleted(it))
    }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        viewModel.onIntent(AiChatUIIntent.GalleryImageSelected(it?.toString()))
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                AiChatUIEffect.LaunchVoiceInput -> try {
                    voiceLauncher.launch(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().toLanguageTag())
                        putExtra(RecognizerIntent.EXTRA_PROMPT, context.getString(R.string.ai_chat_voice_prompt))
                    })
                } catch (_: ActivityNotFoundException) {
                    snackbar.showInfo(context.getString(R.string.ai_chat_voice_unavailable))
                }
                is AiChatUIEffect.LaunchCamera -> cameraLauncher.launch(Uri.parse(effect.uri))
                AiChatUIEffect.LaunchGallery -> galleryLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                )
                is AiChatUIEffect.DialNumber -> try {
                    onDial(effect.number)
                } catch (_: ActivityNotFoundException) {
                    snackbar.showInfo(context.getString(R.string.ai_chat_dialer_unavailable))
                }
                is AiChatUIEffect.ShowMessage -> snackbar.showInfo(
                    ContextCompat.getString(context, effect.messageRes),
                )
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        AiChatScreen(state, onNavigateBack, viewModel::onIntent)
        MedsySnackbarHost(snackbar, Modifier.align(Alignment.BottomCenter))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatScreen(
    state: AiChatState,
    onNavigateBack: () -> Unit,
    onIntent: (AiChatUIIntent) -> Unit,
) {
    Scaffold(
        topBar = {
            AiChatTopBar(
                hasMessages = state.messages.isNotEmpty(),
                onBack = onNavigateBack,
                onNewChat = { onIntent(AiChatUIIntent.NewChatClicked) },
            )
        },
        bottomBar = {
            AiChatComposer(
                value = state.input,
                enabled = state.canSend,
                pendingAttachment = state.pendingAttachment,
                onValueChange = { onIntent(AiChatUIIntent.InputChanged(it)) },
                onAttach = { onIntent(AiChatUIIntent.AttachClicked) },
                onRemoveAttachment = { onIntent(AiChatUIIntent.RemoveAttachmentClicked) },
                onVoice = { onIntent(AiChatUIIntent.VoiceClicked) },
                onSend = { onIntent(AiChatUIIntent.SendClicked) },
            )
        },
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding),
        ) {
            when {
                state.isLoadingHistory -> Box(Modifier.fillMaxSize(), Alignment.BottomStart) {
                    AiChatTypingIndicator()
                }
                state.historyErrorRes != null -> HistoryErrorCard(state.historyErrorRes) {
                    onIntent(AiChatUIIntent.RetryHistory)
                }
                state.messages.isEmpty() -> AiChatWelcome(state.isAdmin, onIntent)
                else -> AiChatConversation(state, onIntent)
            }
        }
    }
    if (state.isAttachSheetVisible) AttachSourceSheet(onIntent)
    if (state.isNewChatDialogVisible) AiChatNewChatDialog(
        onDismiss = { onIntent(AiChatUIIntent.DismissNewChat) },
        onConfirm = { onIntent(AiChatUIIntent.ConfirmNewChat) },
    )
    state.selectedMember?.let { PharmacistDetailsSheet(it, onIntent) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AttachSourceSheet(onIntent: (AiChatUIIntent) -> Unit) {
    ModalBottomSheet(onDismissRequest = { onIntent(AiChatUIIntent.AttachSheetDismissed) }) {
        Column(
            Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(stringResource(R.string.ai_chat_attach_title), fontWeight = FontWeight.Bold)
            AttachOption(Icons.Outlined.CameraAlt, R.string.ai_chat_attach_camera) {
                onIntent(AiChatUIIntent.CameraClicked)
            }
            AttachOption(Icons.Outlined.PhotoLibrary, R.string.ai_chat_attach_gallery) {
                onIntent(AiChatUIIntent.GalleryClicked)
            }
        }
    }
}

@Composable
private fun AttachOption(icon: androidx.compose.ui.graphics.vector.ImageVector, label: Int, onClick: () -> Unit) {
    Surface(onClick = onClick, shape = MaterialTheme.shapes.large, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null)
            Spacer(Modifier.width(12.dp))
            Text(stringResource(label))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PharmacistDetailsSheet(
    pharmacist: PharmacyPharmacist,
    onIntent: (AiChatUIIntent) -> Unit,
) {
    ModalBottomSheet(onDismissRequest = { onIntent(AiChatUIIntent.DismissPharmacistDetails) }) {
        Column(
            Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(pharmacist.fullName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            DetailRow(Icons.Outlined.Email, pharmacist.email)
            DetailRow(Icons.Outlined.Phone, pharmacist.phoneNumber ?: stringResource(R.string.profile_not_provided))
        }
    }
}

@Composable
private fun DetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(12.dp))
        Text(value)
    }
}

@Composable
private fun HistoryErrorCard(@StringRes error: Int, onRetry: () -> Unit) {
    Box(Modifier.fillMaxSize().padding(20.dp), Alignment.Center) {
        Card(colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerLow)) {
            Column(
                Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(stringResource(R.string.ai_chat_history_error_title), fontWeight = FontWeight.Bold)
                Text(stringResource(error), color = MaterialTheme.colorScheme.onSurfaceVariant)
                OutlinedButton(onClick = onRetry, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Outlined.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.ai_chat_history_retry))
                }
            }
        }
    }
}
