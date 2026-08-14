package com.medsy.presentation.aichat

import androidx.annotation.StringRes

sealed interface AiChatUIEffect {
    data object LaunchVoiceInput : AiChatUIEffect
    data class LaunchCamera(val uri: String) : AiChatUIEffect
    data object LaunchGallery : AiChatUIEffect
    data class DialNumber(val number: String) : AiChatUIEffect
    data class ShowMessage(@StringRes val messageRes: Int) : AiChatUIEffect
}
