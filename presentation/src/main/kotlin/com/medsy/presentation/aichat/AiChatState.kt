package com.medsy.presentation.aichat

import androidx.annotation.StringRes
import com.medsy.domain.aichat.model.AiChatImage
import com.medsy.domain.aichat.model.AiChatMessage
import com.medsy.domain.aichat.model.AiChatOutgoingMessage
import com.medsy.domain.pharmacy.model.PharmacyPharmacist

data class AiChatState(
    val messages: List<AiChatMessage> = emptyList(),
    val input: String = "",
    val isResponding: Boolean = false,
    val isLoadingHistory: Boolean = true,
    @StringRes val historyErrorRes: Int? = null,
    @StringRes val errorMessageRes: Int? = null,
    val failedSubmission: AiChatOutgoingMessage? = null,
    val pendingAttachment: AiChatImage? = null,
    val isAttachSheetVisible: Boolean = false,
    val isNewChatDialogVisible: Boolean = false,
    val isAdmin: Boolean = false,
    val pharmacyMembers: List<PharmacyPharmacist> = emptyList(),
    val selectedMember: PharmacyPharmacist? = null,
) {
    val canSend: Boolean get() = !isResponding && !isLoadingHistory
}
