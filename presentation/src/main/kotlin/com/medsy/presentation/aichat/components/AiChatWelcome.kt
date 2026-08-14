package com.medsy.presentation.aichat.components

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Biotech
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.medsy.presentation.R
import com.medsy.presentation.aichat.AiChatUIIntent
import com.medsy.domain.aichat.model.AiAnalyticsPreset

@Composable
fun AiChatWelcome(isAdmin: Boolean, onIntent: (AiChatUIIntent) -> Unit) {
    val actions = if (isAdmin) adminActions() else pharmacistActions()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { AiChatDisclaimer() }
        item {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                AiChatAvatar(64.dp)
                Spacer(Modifier.height(12.dp))
                Text(
                    stringResource(R.string.ai_chat_greeting),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                )
                Text(
                    stringResource(R.string.ai_chat_welcome_supporting),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp),
                )
            }
        }
        actions.chunked(2).forEach { row ->
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    row.forEach { action ->
                        QuickActionCard(action, Modifier.weight(1f), onIntent)
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

private data class QuickAction(
    @param:StringRes val label: Int,
    @param:StringRes val question: Int?,
    val icon: ImageVector,
    val analyticsPreset: AiAnalyticsPreset? = null,
)

@Composable
private fun QuickActionCard(
    action: QuickAction,
    modifier: Modifier,
    onIntent: (AiChatUIIntent) -> Unit,
) {
    val question = action.question?.let { stringResource(it) }
    Card(
        modifier = modifier.clickable {
            if (question == null) onIntent(AiChatUIIntent.AttachClicked)
            else onIntent(AiChatUIIntent.QuickActionClicked(question, action.analyticsPreset))
        },
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(Modifier.padding(14.dp)) {
            Icon(action.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(10.dp))
            Text(
                stringResource(action.label),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                minLines = 2,
            )
        }
    }
}

private fun adminActions() = listOf(
    QuickAction(R.string.ai_chat_quick_month_overview, R.string.ai_chat_quick_month_overview_question,
        Icons.Outlined.Analytics, AiAnalyticsPreset.PHARMACY_MONTH_OVERVIEW),
    QuickAction(R.string.ai_chat_quick_acceptance, R.string.ai_chat_quick_acceptance_question,
        Icons.Outlined.Groups, AiAnalyticsPreset.PHARMACY_MONTH_ACCEPTANCE),
    QuickAction(R.string.ai_chat_quick_top_employee, R.string.ai_chat_quick_top_employee_question,
        Icons.Outlined.Analytics, AiAnalyticsPreset.PHARMACY_MONTH_TOP_EMPLOYEE),
    QuickAction(R.string.ai_chat_quick_largest_order, R.string.ai_chat_quick_largest_order_question,
        Icons.Outlined.Medication, AiAnalyticsPreset.PHARMACY_MONTH_LARGEST_ORDER),
)

private fun pharmacistActions() = listOf(
    QuickAction(R.string.ai_chat_quick_my_overview, R.string.ai_chat_quick_my_overview_question,
        Icons.Outlined.Analytics, AiAnalyticsPreset.SELF_MONTH_OVERVIEW),
    QuickAction(R.string.ai_chat_quick_my_orders, R.string.ai_chat_quick_my_orders_question,
        Icons.Outlined.Groups, AiAnalyticsPreset.SELF_MONTH_ORDERS),
    QuickAction(R.string.ai_chat_quick_ingredient, R.string.ai_chat_quick_ingredient_question, Icons.Outlined.Biotech),
    QuickAction(R.string.ai_chat_quick_photo, null, Icons.Outlined.AddAPhoto),
)
