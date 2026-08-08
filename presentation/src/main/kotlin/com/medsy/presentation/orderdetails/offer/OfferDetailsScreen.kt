package com.medsy.presentation.orderdetails.offer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.designsystem.components.MedsyButton
import com.medsy.designsystem.components.MedsyLottie
import com.medsy.designsystem.ui.theme.extendedColors
import com.medsy.domain.offer.model.Offer
import com.medsy.domain.offer.model.OfferItem
import com.medsy.domain.offer.model.OfferStatusConstants
import com.medsy.presentation.R
import com.medsy.presentation.orderdetails.components.RequestDetailsShimmer
import com.medsy.presentation.orderdetails.components.RequestDetailsTopBar

@Composable
fun OfferDetailsRoot(
    offerId: Long,
    onNavigateBack: () -> Unit,
    viewModel: OfferDetailsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(offerId) { viewModel.onIntent(OfferDetailsUIIntent.Load(offerId)) }
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { onNavigateBack() }
    }
    OfferDetailsScreen(state, viewModel::onIntent)
}

@Composable
fun OfferDetailsScreen(state: OfferDetailsUIState, onIntent: (OfferDetailsUIIntent) -> Unit) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        when {
            state.isLoading -> Box(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) { RequestDetailsShimmer() }

            state.hasError || state.offer == null -> Box(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues), contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(R.string.offer_details_load_error))
                    MedsyButton(
                        onClick = { onIntent(OfferDetailsUIIntent.Retry) },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text(stringResource(R.string.requests_retry))
                    }
                }
            }

            else -> {
                val offer = state.offer
                Column(Modifier
                    .fillMaxSize()
                    .padding(paddingValues)) {
                    RequestDetailsTopBar(
                        isNewOrder = false,
                        onBackClick = { onIntent(OfferDetailsUIIntent.BackClicked) },
                        titleRes = R.string.offer_details_title,
                        modifier = Modifier.padding(horizontal = 12.dp),
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        OfferStatusCard(offer.status, Modifier.padding(top = 16.dp))
                        OfferIdentityCard(offer)
                        OfferProductsCard(offer.items)
                        CustomerContractPendingCard()
                    }
                }
            }
        }
    }
}

@Composable
private fun OfferStatusCard(status: String, modifier: Modifier = Modifier) {
    val rejected = status.equals(OfferStatusConstants.REJECTED, ignoreCase = true)
    StatusCard(modifier) {
        if (rejected) {
            Icon(
                imageVector = Icons.Filled.Cancel,
                contentDescription = null,
                tint = MaterialTheme.extendedColors.redContent,
                modifier = Modifier.size(56.dp),
            )
            Text(stringResource(R.string.offer_details_rejected), fontWeight = FontWeight.Bold)
        } else {
            MedsyLottie(R.raw.onboarding_track_order, modifier = Modifier.size(160.dp))
            Text(stringResource(R.string.offer_details_waiting), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun OfferIdentityCard(offer: Offer) {
    StatusCard {
        Text(
            stringResource(R.string.offer_details_offer_number, offer.id),
            fontWeight = FontWeight.Bold
        )
        Text(
            stringResource(R.string.offer_details_request_number, offer.requestId),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun OfferProductsCard(items: List<OfferItem>) {
    StatusCard {
        Text(
            stringResource(R.string.request_details_requested_medicines),
            fontWeight = FontWeight.Bold
        )
        items.forEach { item ->
            val title = item.productName.ifBlank {
                stringResource(
                    R.string.offer_details_product_fallback,
                    item.productId
                )
            }
            Text(title, style = MaterialTheme.typography.bodyLarge)
            listOfNotNull(item.strength, item.packSize, item.form)
                .filter(String::isNotBlank)
                .joinToString(" • ")
                .takeIf(String::isNotBlank)
                ?.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
        }
    }
}

@Composable
private fun CustomerContractPendingCard() {
    StatusCard {
        Text(
            stringResource(R.string.offer_details_customer_unavailable),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StatusCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = content,
        )
    }
}
