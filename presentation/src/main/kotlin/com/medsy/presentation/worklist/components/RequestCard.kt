package com.medsy.presentation.worklist.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.medsy.domain.orders.model.PaymentMethod
import com.medsy.domain.orders.model.PharmacyOrder
import com.medsy.domain.orders.model.PharmacyRequest
import com.medsy.presentation.R
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun PharmacyRequestCard(
    request: PharmacyRequest,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PharmacyWorkCardLayout(
        id = request.id,
        customerName = request.customerName,
        customerId = request.customerId,
        customerPhone = request.customerPhone,
        customerAddress = request.deliveryAddress,
        productImages = request.items.map { it.imageUrl },
        total = request.items.sumOf { it.unitPrice * it.quantity },
        paymentMethod = request.paymentMethod,
        trailing = {
            RequestStatusBadge(
                requestStatus = request.requestStatus,
                assignmentStatus = request.assignmentStatus,
            )
        },
        timeLabel = request.createdAt.minutesAgoLabel(),
        onClick = onClick,
        modifier = modifier,
    )
}

@Composable
fun PharmacyOrderCard(
    order: PharmacyOrder,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PharmacyWorkCardLayout(
        id = order.id,
        customerName = order.customerName,
        customerId = order.customerId,
        customerPhone = order.customerPhone,
        customerAddress = order.deliveryAddress,
        productImages = order.items.map { it.imageUrl },
        total = order.total,
        paymentMethod = order.paymentMethod,
        trailing = { OrderStatusBadge(order.status) },
        timeLabel = order.createdAt.localizedOrderDate(),
        onClick = onClick,
        modifier = modifier,
    )
}

@Composable
private fun PharmacyWorkCardLayout(
    id: Long,
    customerName: String?,
    customerId: Long,
    customerPhone: String?,
    customerAddress: String?,
    productImages: List<String?>,
    total: Double,
    paymentMethod: PaymentMethod,
    trailing: @Composable () -> Unit,
    timeLabel: String?,
    onClick: () -> Unit,
    modifier: Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    stringResource(R.string.request_details_request_number_format, id.toString()),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    timeLabel?.takeIf(String::isNotBlank)?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(
                        Icons.Filled.ShoppingBag,
                        contentDescription = stringResource(R.string.orders_work_item_icon_desc),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }
            Row(
                Modifier.fillMaxWidth().padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    customerName ?: stringResource(R.string.customer_default_format, customerId),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
                trailing()
            }
            customerPhone?.takeIf(String::isNotBlank)?.let {
                Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            customerAddress?.takeIf(String::isNotBlank)?.let {
                Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            val validImages = productImages.filterNotNull().filter(String::isNotBlank).take(5)
            if (validImages.isNotEmpty()) {
                Row(
                    Modifier.fillMaxWidth().padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    validImages.forEach { imageUrl ->
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = stringResource(R.string.orders_product_image_desc),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(32.dp).clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                        )
                    }
                }
            }
            Row(
                Modifier.fillMaxWidth().padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    stringResource(R.string.request_details_price_egp, total),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    stringResource(
                        R.string.requests_payment_prefix,
                        when (paymentMethod) {
                            PaymentMethod.Cash -> stringResource(R.string.requests_payment_cash)
                            PaymentMethod.Card -> stringResource(R.string.requests_payment_card)
                            PaymentMethod.Unknown -> stringResource(R.string.requests_payment_unknown)
                        },
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun String.minutesAgoLabel(): String? {
    if (!contains('T')) return null
    val minutes = runCatching {
        val normalized = if (endsWith("Z")) this else "${this}Z"
        Duration.between(Instant.parse(normalized), Instant.now()).toMinutes().coerceAtLeast(0).toInt()
    }.getOrNull() ?: return null
    return if (minutes >= 60) {
        stringResource(R.string.request_details_hours_minutes_ago, minutes / 60, minutes % 60)
    } else stringResource(R.string.request_details_minutes_ago, minutes)
}

@Composable
private fun String?.localizedOrderDate(): String? {
    val raw = this?.takeIf(String::isNotBlank) ?: return null
    val locale = LocalConfiguration.current.locales[0]
    return runCatching {
        LocalDate.parse(raw.substringBefore('T')).format(
            DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)
        )
    }.getOrElse { raw }
}
