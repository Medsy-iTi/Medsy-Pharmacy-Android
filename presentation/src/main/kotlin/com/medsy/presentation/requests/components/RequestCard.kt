package com.medsy.presentation.requests.components

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.medsy.designsystem.components.MedsyButton
import com.medsy.presentation.R
import com.medsy.presentation.requests.PaymentMethod
import com.medsy.presentation.requests.PharmacyWorkItem

@Composable
fun PharmacyWorkCard(
    item: PharmacyWorkItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(
                        R.string.request_details_request_number_format,
                        item.displayId
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    item.minutesAgo?.let { minutes ->
                        Text(
                            text = if (minutes >= 60) {
                                stringResource(
                                    R.string.request_details_hours_minutes_ago,
                                    minutes / 60,
                                    minutes % 60
                                )
                            } else {
                                stringResource(R.string.request_details_minutes_ago, minutes)
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Icon(
                        imageVector = Icons.Filled.ShoppingBag,
                        contentDescription = stringResource(R.string.orders_work_item_icon_desc),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val customerLabel = item.customerName
                    ?: item.customerId?.let { stringResource(R.string.customer_default_format, it) }
                    ?: stringResource(R.string.orders_customer_pending)
                Text(
                    text = customerLabel,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
                RequestStatusBadge(status = item.status)
            }

            item.customerPhone?.takeIf(String::isNotBlank)?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            item.customerAddress?.takeIf(String::isNotBlank)?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (item.productImages.any { !it.isNullOrBlank() }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    item.productImages.filterNotNull().filter(String::isNotBlank).take(5)
                        .forEach { imageUrl ->
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = stringResource(R.string.orders_product_image_desc),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                            )
                        }
                }
            }

            if (item.total != null || item.paymentMethod != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    item.total?.let {
                        Text(
                            text = stringResource(R.string.request_details_price_egp, it),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    item.paymentMethod?.let {
                        Text(
                            text = stringResource(
                                R.string.requests_payment_prefix,
                                item.paymentLabel()
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            MedsyButton(onClick = onClick, modifier = Modifier.padding(top = 14.dp)) {
                Text(
                    stringResource(R.string.view_order_details),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
private fun PharmacyWorkItem.paymentLabel(): String = when (paymentMethod) {
    PaymentMethod.Cash -> stringResource(R.string.requests_payment_cash)
    PaymentMethod.Visa -> stringResource(
        R.string.requests_payment_card_format,
        stringResource(R.string.requests_payment_visa),
        paymentCardLastDigits.orEmpty()
    )

    PaymentMethod.Mastercard -> stringResource(
        R.string.requests_payment_card_format,
        stringResource(R.string.requests_payment_mastercard),
        paymentCardLastDigits.orEmpty()
    )

    null -> ""
}
