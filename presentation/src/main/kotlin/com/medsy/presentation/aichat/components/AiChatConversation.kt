package com.medsy.presentation.aichat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.medsy.domain.aichat.model.AiChatContent
import com.medsy.domain.aichat.model.AiChatIntent
import com.medsy.domain.aichat.model.AiChatMessage
import com.medsy.presentation.R
import com.medsy.presentation.aichat.AiChatState
import com.medsy.presentation.aichat.AiChatUIIntent

@Composable
fun AiChatConversation(state: AiChatState, onIntent: (AiChatUIIntent) -> Unit) {
    val listState = rememberLazyListState()
    LaunchedEffect(state.messages.size, state.isResponding, state.errorMessageRes) {
        val footer = if (state.isResponding || state.errorMessageRes != null) 1 else 0
        val index = state.messages.lastIndex + footer
        if (index >= 0) listState.animateScrollToItem(index)
    }
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(state.messages, key = AiChatMessage::id) { message ->
            when (val content = message.content) {
                is AiChatContent.UserText -> UserBubble(content)
                is AiChatContent.AssistantMessage -> AssistantMessage(
                    content = content,
                    availablePharmacistIds = state.pharmacyMembers.map { it.id }.toSet(),
                    onIntent = onIntent,
                )
            }
        }
        if (state.isResponding) item(key = "typing") { AiChatTypingIndicator() }
        state.errorMessageRes?.let { error ->
            item(key = "error") {
                SendRetryCard(error) { onIntent(AiChatUIIntent.RetrySend) }
            }
        }
    }
}

@Composable
private fun UserBubble(content: AiChatContent.UserText) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .background(
                    MaterialTheme.colorScheme.primary,
                    RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp),
                )
                .padding(horizontal = 14.dp, vertical = 11.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            content.imageUri?.let { uri ->
                AsyncImage(
                    model = uri,
                    contentDescription = stringResource(R.string.ai_chat_sent_photo_description),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(12.dp)),
                )
            }
            if (content.value.isNotBlank()) Text(
                text = content.value,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun AssistantMessage(
    content: AiChatContent.AssistantMessage,
    availablePharmacistIds: Set<Long>,
    onIntent: (AiChatUIIntent) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(Modifier.fillMaxWidth(0.92f), verticalAlignment = Alignment.Top) {
            AiChatAvatar(28.dp)
            Spacer(Modifier.width(8.dp))
            ChatMarkdownText(
                text = content.answer.ifBlank {
                    stringResource(R.string.ai_chat_catalog_empty_answer)
                },
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surfaceContainerLow,
                        RoundedCornerShape(4.dp, 18.dp, 18.dp, 18.dp),
                    )
                    .padding(horizontal = 14.dp, vertical = 11.dp),
            )
        }
        if (content.intent == AiChatIntent.EMERGENCY && content.emergencyNumbers.isNotEmpty()) {
            AiChatEmergencyCard(
                numbers = content.emergencyNumbers,
                onCall = { number ->
                    onIntent(AiChatUIIntent.EmergencyCallClicked(number))
                },
            )
        }
        if (content.intent == AiChatIntent.DOCTOR_SPECIALIZATION &&
            content.doctorSpecializations.isNotEmpty()
        ) AiChatSpecializationCard(content.doctorSpecializations)
        content.categories.forEach { AiChatCategoryCard(it) }
        content.pharmacistRankings.forEach { ranking ->
            AiChatRankingCard(ranking) {
                onIntent(AiChatUIIntent.PharmacistClicked(it))
            }
        }
        content.analytics?.let { analytics ->
            AiChatAnalyticsCard(analytics, availablePharmacistIds) {
                onIntent(AiChatUIIntent.PharmacistClicked(it))
            }
        }
        content.products.forEach { AiChatProductCard(it) }
        content.disclaimer?.let { AiChatMessageDisclaimer(it) }
    }
}

@Composable
private fun SendRetryCard(errorRes: Int, onRetry: () -> Unit) {
    Card(colors = CardDefaults.cardColors(MaterialTheme.colorScheme.errorContainer)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.ai_chat_catalog_error_title), fontWeight = FontWeight.Bold)
            Text(stringResource(errorRes), style = MaterialTheme.typography.bodySmall)
            OutlinedButton(onClick = onRetry, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Outlined.Refresh, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.ai_chat_catalog_retry))
            }
        }
    }
}
