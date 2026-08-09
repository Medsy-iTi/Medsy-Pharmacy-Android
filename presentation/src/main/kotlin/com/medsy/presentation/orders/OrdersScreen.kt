package com.medsy.presentation.orders

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.designsystem.components.MedsyButton
import com.medsy.designsystem.components.MedsyLottie
import com.medsy.designsystem.ui.theme.extendedColors
import com.medsy.presentation.R
import com.medsy.presentation.requests.OrdersFilter
import com.medsy.presentation.requests.components.PharmacyWorkCard
import com.medsy.presentation.requests.components.RequestsSearchBar
import com.medsy.presentation.requests.components.RequestsShimmer

@Composable
fun OrdersRoot(
    onOfferClick: (Long) -> Unit,
    onOrderClick: (Long) -> Unit,
    viewModel: OrdersViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is OrdersUIEffect.NavigateToOfferDetails -> onOfferClick(effect.offerId)
                is OrdersUIEffect.NavigateToOrderDetails -> onOrderClick(effect.orderId)
            }
        }
    }
    OrdersScreen(state = state, onIntent = viewModel::onIntent)
}

@Composable
fun OrdersScreen(
    state: OrdersUIState,
    onIntent: (OrdersUIIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Text(
                text = stringResource(R.string.orders_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.extendedColors.darkBlueColor,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            OrdersFiltersRow(
                selected = state.selectedFilter,
                onSelected = { onIntent(OrdersUIIntent.FilterSelected(it)) },
            )
            RequestsSearchBar(
                query = state.searchQuery,
                onQueryChange = { onIntent(OrdersUIIntent.SearchQueryChanged(it)) },
                modifier = Modifier.padding(top = 12.dp),
            )

            when {
                state.isLoading -> RequestsShimmer()
                state.hasError -> OrdersMessage(
                    message = stringResource(R.string.orders_load_error),
                    action = stringResource(R.string.requests_retry),
                    onAction = { onIntent(OrdersUIIntent.Retry) },
                )

                state.visibleItems.isEmpty() -> OrdersMessage(stringResource(R.string.orders_empty))
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    itemsIndexed(
                        state.visibleItems,
                        key = { _, item -> item.stableKey }) { index, item ->
                        PharmacyWorkCard(
                            item = item,
                            onClick = {
                                onIntent(
                                    OrdersUIIntent.ItemClicked(
                                        item.id,
                                        item.source
                                    )
                                )
                            },
                        )
                        if (index == state.visibleItems.lastIndex && state.canLoadMore) {
                            LaunchedEffect(item.stableKey, state.selectedFilter) {
                                onIntent(
                                    OrdersUIIntent.LoadMore
                                )
                            }
                        }
                    }
                    if (state.isLoadingMore) {
                        item {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .size(32.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrdersFiltersRow(selected: OrdersFilter, onSelected: (OrdersFilter) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OrdersFilter.entries.forEach { filter ->
            FilterChip(
                selected = selected == filter,
                onClick = { onSelected(filter) },
                label = { Text(stringResource(filter.labelRes())) },
            )
        }
    }
}

private fun OrdersFilter.labelRes(): Int = when (this) {
    OrdersFilter.All -> R.string.orders_filter_all
    OrdersFilter.ActiveOffers -> R.string.orders_filter_active_offers
    OrdersFilter.RejectedOffers -> R.string.orders_filter_rejected_offers
    OrdersFilter.InProgress -> R.string.orders_filter_in_progress
    OrdersFilter.Delivered -> R.string.orders_filter_delivered
}

@Composable
private fun OrdersMessage(message: String, action: String? = null, onAction: () -> Unit = {}) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        MedsyLottie(com.medsy.designsystem.R.raw.no_data_found, modifier = Modifier.size(180.dp))
        Text(
            message,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        action?.let {
            MedsyButton(
                onClick = onAction,
                modifier = Modifier.padding(top = 16.dp)
            ) { Text(it) }
        }
    }
}
