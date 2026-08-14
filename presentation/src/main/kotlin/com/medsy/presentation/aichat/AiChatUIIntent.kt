package com.medsy.presentation.aichat

sealed interface AiChatUIIntent {
    data class InputChanged(val value: String) : AiChatUIIntent
    data object SendClicked : AiChatUIIntent
    data class QuickActionClicked(val question: String) : AiChatUIIntent
    data object RetrySend : AiChatUIIntent
    data object RetryHistory : AiChatUIIntent
    data object VoiceClicked : AiChatUIIntent
    data class VoiceResult(val text: String?) : AiChatUIIntent
    data object NewChatClicked : AiChatUIIntent
    data object DismissNewChat : AiChatUIIntent
    data object ConfirmNewChat : AiChatUIIntent
    data object AttachClicked : AiChatUIIntent
    data object AttachSheetDismissed : AiChatUIIntent
    data object CameraClicked : AiChatUIIntent
    data object GalleryClicked : AiChatUIIntent
    data class CameraCaptureCompleted(val success: Boolean) : AiChatUIIntent
    data class GalleryImageSelected(val uri: String?) : AiChatUIIntent
    data object RemoveAttachmentClicked : AiChatUIIntent
    data class EmergencyCallClicked(val number: String) : AiChatUIIntent
    data class PharmacistClicked(val pharmacistId: Long) : AiChatUIIntent
    data object DismissPharmacistDetails : AiChatUIIntent
}
