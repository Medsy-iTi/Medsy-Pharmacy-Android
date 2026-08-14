package com.medsy.presentation.aichat.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medsy.domain.aichat.model.AiPerformanceDirection
import com.medsy.domain.aichat.model.AiPerformanceMetric
import com.medsy.domain.aichat.model.AiPerformancePeriod
import com.medsy.domain.aichat.model.AiPharmacistRanking
import com.medsy.presentation.R

@Composable
fun AiChatRankingCard(
    ranking: AiPharmacistRanking,
    onPharmacistClick: (Long) -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = stringResource(ranking.metric.labelRes()),
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = stringResource(ranking.period.labelRes()),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(50),
                ) {
                    Text(
                        text = stringResource(ranking.direction.labelRes()),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    )
                }
            }
            if (ranking.entries.isEmpty()) {
                Text(
                    text = stringResource(R.string.ai_chat_ranking_empty),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else ranking.entries.forEach { entry ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPharmacistClick(entry.pharmacistId) }
                        .padding(vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Surface(
                            modifier = Modifier.matchParentSize(),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                        ) {}
                        Text(entry.rank.toString(), fontWeight = FontWeight.Bold)
                    }
                    Text(entry.fullName, modifier = Modifier.weight(1f))
                    Text(
                        text = stringResource(R.string.ai_chat_ranking_count, entry.count),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

private fun AiPerformanceMetric.labelRes() = when (this) {
    AiPerformanceMetric.OFFERS_CREATED -> R.string.ai_chat_metric_offers
    AiPerformanceMetric.SUCCESSFUL_ORDERS -> R.string.ai_chat_metric_orders
}

private fun AiPerformancePeriod.labelRes() = when (this) {
    AiPerformancePeriod.LAST_DAY -> R.string.ai_chat_period_day
    AiPerformancePeriod.LAST_WEEK -> R.string.ai_chat_period_week
    AiPerformancePeriod.LAST_MONTH -> R.string.ai_chat_period_month
    AiPerformancePeriod.LAST_YEAR -> R.string.ai_chat_period_year
}

private fun AiPerformanceDirection.labelRes() = when (this) {
    AiPerformanceDirection.TOP -> R.string.ai_chat_direction_top
    AiPerformanceDirection.BOTTOM -> R.string.ai_chat_direction_bottom
}
