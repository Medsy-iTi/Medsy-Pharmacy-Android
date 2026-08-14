package com.medsy.presentation.aichat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.aichat.model.AiChatImage
import com.medsy.domain.aichat.model.AiChatOutgoingMessage
import com.medsy.domain.aichat.usecase.DeleteAiChatImageUseCase
import com.medsy.domain.aichat.usecase.ImportAiChatImageUseCase
import com.medsy.domain.aichat.usecase.LoadAiChatHistoryUseCase
import com.medsy.domain.aichat.usecase.ObserveAiChatSessionUseCase
import com.medsy.domain.aichat.usecase.PrepareAiChatImageUseCase
import com.medsy.domain.aichat.usecase.SendAiChatMessageUseCase
import com.medsy.domain.aichat.usecase.StartNewAiChatUseCase
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.domain.pharmacy.usecase.GetMyPharmacyUseCase
import com.medsy.presentation.R
import com.medsy.presentation.common.util.toMessageRes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AiChatViewModel @Inject constructor(
    observeSession: ObserveAiChatSessionUseCase,
    private val loadHistory: LoadAiChatHistoryUseCase,
    private val sendMessage: SendAiChatMessageUseCase,
    private val startNewChat: StartNewAiChatUseCase,
    private val prepareImage: PrepareAiChatImageUseCase,
    private val importImage: ImportAiChatImageUseCase,
    private val deleteImage: DeleteAiChatImageUseCase,
    private val getMyPharmacy: GetMyPharmacyUseCase,
) : ViewModel() {
    private val mutableState = MutableStateFlow(AiChatState())
    val state = mutableState.asStateFlow()
    private val mutableEffect = Channel<AiChatUIEffect>(Channel.BUFFERED)
    val effect = mutableEffect.receiveAsFlow()
    private var pendingCameraImage: AiChatImage? = null

    init {
        viewModelScope.launch {
            observeSession().collect { session ->
                mutableState.update {
                    it.copy(
                        messages = session.messages,
                        isResponding = session.isResponding,
                        isLoadingHistory = !session.isHydrated && it.historyErrorRes == null,
                    )
                }
            }
        }
        fetchHistory()
        viewModelScope.launch {
            getMyPharmacy().onSuccess { pharmacy ->
                mutableState.update {
                    it.copy(isAdmin = pharmacy.isAdmin, pharmacyMembers = pharmacy.pharmacists)
                }
            }
        }
    }

    fun onIntent(intent: AiChatUIIntent) {
        when (intent) {
            is AiChatUIIntent.InputChanged -> mutableState.update {
                it.copy(input = intent.value.take(MAX_MESSAGE_LENGTH))
            }
            AiChatUIIntent.SendClicked -> sendCurrentInput()
            is AiChatUIIntent.QuickActionClicked -> submit(
                AiChatOutgoingMessage.Text(intent.question, intent.analyticsPreset),
            )
            AiChatUIIntent.RetrySend -> mutableState.value.failedSubmission?.let(::submit)
            AiChatUIIntent.RetryHistory -> fetchHistory()
            AiChatUIIntent.VoiceClicked -> sendEffect(AiChatUIEffect.LaunchVoiceInput)
            is AiChatUIIntent.VoiceResult -> intent.text?.takeIf(String::isNotBlank)?.let { value ->
                mutableState.update { it.copy(input = value.take(MAX_MESSAGE_LENGTH)) }
            }
            AiChatUIIntent.NewChatClicked -> mutableState.update {
                it.copy(isNewChatDialogVisible = true)
            }
            AiChatUIIntent.DismissNewChat -> mutableState.update {
                it.copy(isNewChatDialogVisible = false)
            }
            AiChatUIIntent.ConfirmNewChat -> confirmNewChat()
            AiChatUIIntent.AttachClicked -> mutableState.update { it.copy(isAttachSheetVisible = true) }
            AiChatUIIntent.AttachSheetDismissed -> mutableState.update {
                it.copy(isAttachSheetVisible = false)
            }
            AiChatUIIntent.CameraClicked -> launchCamera()
            AiChatUIIntent.GalleryClicked -> {
                mutableState.update { it.copy(isAttachSheetVisible = false) }
                sendEffect(AiChatUIEffect.LaunchGallery)
            }
            is AiChatUIIntent.CameraCaptureCompleted -> onCameraResult(intent.success)
            is AiChatUIIntent.GalleryImageSelected -> onGalleryResult(intent.uri)
            AiChatUIIntent.RemoveAttachmentClicked -> removeAttachment()
            is AiChatUIIntent.EmergencyCallClicked -> sendEffect(AiChatUIEffect.DialNumber(intent.number))
            is AiChatUIIntent.PharmacistClicked -> {
                val member = mutableState.value.pharmacyMembers
                    .firstOrNull { it.id == intent.pharmacistId }
                if (member == null) sendEffect(AiChatUIEffect.ShowMessage(R.string.ai_chat_member_unavailable))
                else mutableState.update { it.copy(selectedMember = member) }
            }
            AiChatUIIntent.DismissPharmacistDetails -> mutableState.update {
                it.copy(selectedMember = null)
            }
        }
    }

    private fun fetchHistory() {
        mutableState.update { it.copy(isLoadingHistory = true, historyErrorRes = null) }
        viewModelScope.launch {
            loadHistory().onSuccess {
                mutableState.update { it.copy(isLoadingHistory = false) }
            }.onError { error ->
                mutableState.update {
                    it.copy(isLoadingHistory = false, historyErrorRes = error.toMessageRes())
                }
            }
        }
    }

    private fun sendCurrentInput() {
        val current = mutableState.value
        val text = current.input.trim()
        val message = when {
            current.pendingAttachment != null -> AiChatOutgoingMessage.Image(
                current.pendingAttachment,
                text,
            )
            text.isNotEmpty() -> AiChatOutgoingMessage.Text(text)
            else -> return
        }
        submit(message)
    }

    private fun submit(message: AiChatOutgoingMessage) {
        if (!mutableState.value.canSend) return
        mutableState.update {
            it.copy(
                input = "",
                pendingAttachment = null,
                failedSubmission = null,
                errorMessageRes = null,
            )
        }
        viewModelScope.launch {
            sendMessage(message).onError { error ->
                mutableState.update {
                    it.copy(failedSubmission = message, errorMessageRes = error.toMessageRes())
                }
            }
        }
    }

    private fun confirmNewChat() {
        mutableState.update { it.copy(isNewChatDialogVisible = false) }
        viewModelScope.launch {
            startNewChat()
                .onSuccess {
                    val attachment = mutableState.value.pendingAttachment
                    mutableState.update {
                        it.copy(
                            input = "",
                            pendingAttachment = null,
                            failedSubmission = null,
                            errorMessageRes = null,
                            selectedMember = null,
                        )
                    }
                    attachment?.let { deleteImage(it) }
                }
                .onError { sendEffect(AiChatUIEffect.ShowMessage(it.toMessageRes())) }
        }
    }

    private fun launchCamera() {
        mutableState.update { it.copy(isAttachSheetVisible = false) }
        viewModelScope.launch {
            prepareImage().onSuccess { image ->
                pendingCameraImage = image
                sendEffect(AiChatUIEffect.LaunchCamera(image.uri))
            }.onError { sendEffect(AiChatUIEffect.ShowMessage(it.toMessageRes())) }
        }
    }

    private fun onCameraResult(success: Boolean) {
        val image = pendingCameraImage ?: return
        pendingCameraImage = null
        if (success) setAttachment(image) else viewModelScope.launch { deleteImage(image) }
    }

    private fun onGalleryResult(uri: String?) {
        if (uri == null) return
        viewModelScope.launch {
            importImage(uri).onSuccess(::setAttachment)
                .onError { sendEffect(AiChatUIEffect.ShowMessage(it.toMessageRes())) }
        }
    }

    private fun setAttachment(image: AiChatImage) {
        val previous = mutableState.value.pendingAttachment
        mutableState.update { it.copy(pendingAttachment = image) }
        previous?.let { viewModelScope.launch { deleteImage(it) } }
    }

    private fun removeAttachment() {
        val image = mutableState.value.pendingAttachment ?: return
        mutableState.update { it.copy(pendingAttachment = null) }
        viewModelScope.launch { deleteImage(image) }
    }

    private fun sendEffect(effect: AiChatUIEffect) {
        viewModelScope.launch { mutableEffect.send(effect) }
    }

    private companion object { const val MAX_MESSAGE_LENGTH = 500 }
}
