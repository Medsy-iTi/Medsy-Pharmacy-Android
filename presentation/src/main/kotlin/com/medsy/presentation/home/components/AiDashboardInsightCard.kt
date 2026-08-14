package com.medsy.presentation.home.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medsy.domain.dashboard.model.AiDashboardSummary
import com.medsy.presentation.R
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@Composable
fun AiDashboardInsightCard(
    summary: AiDashboardSummary?,
    isLoading: Boolean,
    @StringRes errorRes: Int?,
    onRetry: () -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    stringResource(R.string.home_ai_insight_title),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                if (summary?.cached == true) Text(
                    stringResource(R.string.home_ai_insight_cached),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            when {
                isLoading -> Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(10.dp))
                    Text(stringResource(R.string.home_ai_insight_loading))
                }
                errorRes != null -> {
                    Text(stringResource(errorRes), style = MaterialTheme.typography.bodySmall)
                    TextButton(onClick = onRetry) {
                        Text(stringResource(R.string.home_ai_insight_retry))
                    }
                }
                summary != null -> {
                    AiDashboardMarkdownText(
                        text = summary.summary,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    if (summary.generatedAt.isNotBlank()) Text(
                        stringResource(
                            R.string.home_ai_insight_updated,
                            summary.generatedAt.toLocalizedDateTime(),
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
        }
    }
}

private fun String.toLocalizedDateTime(): String = runCatching {
    DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
        .withLocale(Locale.getDefault())
        .withZone(ZoneId.systemDefault())
        .format(Instant.parse(this))
}.getOrDefault(this)
