package com.medsy.presentation.aichat.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.medsy.domain.aichat.model.AiAnalyticsBreakdown
import com.medsy.domain.aichat.model.AiAnalyticsMetric
import com.medsy.domain.aichat.model.AiChatAnalytics
import com.medsy.presentation.R
import java.text.NumberFormat
import java.time.LocalDateTime
import java.util.Locale

@Composable
fun AiChatAnalyticsCard(
    analytics: AiChatAnalytics,
    availablePharmacistIds: Set<Long>,
    onPharmacistClick: (Long) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.ai_chat_analytics_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(
                    R.string.ai_chat_analytics_period,
                    analytics.start.take(10),
                    inclusiveEndDate(analytics.end),
                ),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            analytics.metrics.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    row.forEach { metric -> MetricCell(metric, Modifier.weight(1f)) }
                    if (row.size == 1) Column(Modifier.weight(1f)) {}
                }
            }

            analytics.breakdowns.groupBy(AiAnalyticsBreakdown::group).forEach { (group, values) ->
                HorizontalDivider()
                BreakdownBars(group, values)
            }

            if (analytics.rankings.isNotEmpty()) {
                HorizontalDivider()
                Text(
                    stringResource(R.string.ai_chat_analytics_team_ranking),
                    fontWeight = FontWeight.Bold,
                )
                analytics.rankings.forEach { entry ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                enabled = entry.pharmacistId in availablePharmacistIds,
                            ) { onPharmacistClick(entry.pharmacistId) }
                            .padding(vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text("#${entry.rank}", fontWeight = FontWeight.Bold)
                        Text(entry.fullName, modifier = Modifier.weight(1f))
                        Text(
                            stringResource(R.string.ai_chat_analytics_generated_orders, entry.count),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            analytics.orderHighlights.forEach { order ->
                HorizontalDivider()
                Text(
                    stringResource(R.string.ai_chat_analytics_order_highlight),
                    fontWeight = FontWeight.Bold,
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(stringResource(R.string.ai_chat_analytics_order_id, order.orderId))
                    Text(formatMoney(order.totalPrice), fontWeight = FontWeight.Bold)
                }
                Text(
                    text = "${statusLabel(order.status)} • ${order.date.take(10)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            if (analytics.topProducts.isNotEmpty()) {
                HorizontalDivider()
                Text(
                    stringResource(R.string.ai_chat_analytics_top_products),
                    fontWeight = FontWeight.Bold,
                )
                analytics.topProducts.forEachIndexed { index, product ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text("${index + 1}.")
                        Text(
                            product.productName,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            stringResource(R.string.ai_chat_analytics_product_quantity, product.quantity),
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            if (analytics.metrics.isEmpty() && analytics.breakdowns.isEmpty() &&
                analytics.rankings.isEmpty() && analytics.orderHighlights.isEmpty() &&
                analytics.topProducts.isEmpty()
            ) {
                Text(
                    stringResource(R.string.ai_chat_analytics_empty),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun MetricCell(metric: AiAnalyticsMetric, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        ),
    ) {
        Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = metricLabel(metric.key),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                minLines = 2,
            )
            Text(
                text = formatMetric(metric),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            metric.deltaPercent?.let { delta ->
                Text(
                    stringResource(R.string.ai_chat_analytics_delta, delta),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

@Composable
private fun BreakdownBars(group: String, values: List<AiAnalyticsBreakdown>) {
    Text(
        text = if (group == "OFFERS") {
            stringResource(R.string.ai_chat_analytics_offer_breakdown)
        } else {
            stringResource(R.string.ai_chat_analytics_order_breakdown)
        },
        fontWeight = FontWeight.Bold,
    )
    val max = values.maxOfOrNull(AiAnalyticsBreakdown::count)?.coerceAtLeast(1L) ?: 1L
    values.forEach { item ->
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(statusLabel(item.key), style = MaterialTheme.typography.bodySmall)
            Text(item.count.toString(), fontWeight = FontWeight.SemiBold)
        }
        LinearProgressIndicator(
            progress = { item.count.toFloat() / max.toFloat() },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun metricLabel(key: String): String = stringResource(
    when (key) {
        "REQUESTS_RECEIVED" -> R.string.ai_chat_analytics_requests_received
        "REQUESTS_COVERED" -> R.string.ai_chat_analytics_requests_covered
        "REQUEST_COVERAGE_RATE" -> R.string.ai_chat_analytics_request_coverage
        "OFFERS_CREATED" -> R.string.ai_chat_analytics_offers_created
        "ACCEPTED_OFFERS" -> R.string.ai_chat_analytics_accepted_offers
        "OFFER_ACCEPTANCE_RATE" -> R.string.ai_chat_analytics_offer_acceptance
        "ORDERS_GENERATED" -> R.string.ai_chat_analytics_orders_generated
        "DELIVERED_ORDERS" -> R.string.ai_chat_analytics_delivered_orders
        "CANCELLED_ORDERS" -> R.string.ai_chat_analytics_cancelled_orders
        "TOTAL_ORDER_VALUE" -> R.string.ai_chat_analytics_total_order_value
        "DELIVERED_REVENUE" -> R.string.ai_chat_analytics_delivered_revenue
        "AVERAGE_ORDER_VALUE" -> R.string.ai_chat_analytics_average_order_value
        else -> R.string.ai_chat_analytics_metric_unknown
    },
)

@Composable
private fun statusLabel(status: String): String = stringResource(
    when (status) {
        "PENDING" -> R.string.ai_chat_status_pending
        "ACCEPTED" -> R.string.ai_chat_status_accepted
        "PARTIALLY_ACCEPTED" -> R.string.ai_chat_status_partially_accepted
        "REJECTED" -> R.string.ai_chat_status_rejected
        "EXPIRED" -> R.string.ai_chat_status_expired
        "PENDING_PAYMENT" -> R.string.ai_chat_status_pending_payment
        "PREPARING" -> R.string.ai_chat_status_preparing
        "READY_FOR_PICKUP" -> R.string.ai_chat_status_ready_pickup
        "READY_FOR_DELIVERY" -> R.string.ai_chat_status_ready_delivery
        "OUT_FOR_DELIVERY" -> R.string.ai_chat_status_out_delivery
        "DELIVERED" -> R.string.ai_chat_status_delivered
        "CANCELLED" -> R.string.ai_chat_status_cancelled
        else -> R.string.ai_chat_status_unknown
    },
)

private fun formatMetric(metric: AiAnalyticsMetric): String = when (metric.unit) {
    "EGP" -> formatMoney(metric.value)
    "PERCENT" -> String.format(Locale.getDefault(), "%.1f%%", metric.value)
    else -> NumberFormat.getIntegerInstance().format(metric.value)
}

private fun formatMoney(value: Double): String =
    String.format(Locale.getDefault(), "%,.2f EGP", value)

private fun inclusiveEndDate(end: String): String = runCatching {
    LocalDateTime.parse(end).minusNanos(1).toLocalDate().toString()
}.getOrElse { end.take(10) }
