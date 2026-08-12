package com.medsy.presentation.requests

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
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
import com.medsy.presentation.requests.components.PharmacyWorkCard
import com.medsy.presentation.requests.components.RequestsSearchBar
import com.medsy.presentation.requests.components.RequestsShimmer

@Composable
fun RequestsRoot(
    onRequestClick: (Long) -> Unit,
    viewModel: RequestsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is RequestsUIEffect.NavigateToRequestDetails -> onRequestClick(effect.requestId)
            }
        }
    }

    RequestsScreen(state, viewModel::onIntent)
}

@Composable
fun RequestsScreen(
    state: RequestsUIState,
    onIntent: (RequestsUIIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Text(
                text = stringResource(R.string.requests_title),
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
            RequestsFiltersRow(
                selected = state.selectedFilter,
                onSelected = { onIntent(RequestsUIIntent.FilterSelected(it)) },
            )
            RequestsSearchBar(
                query = state.searchQuery,
                onQueryChange = { onIntent(RequestsUIIntent.SearchQueryChanged(it)) },
                modifier = Modifier.padding(top = 12.dp),
            )

            when {
                state.isLoading -> RequestsShimmer()
                state.hasError -> RequestsMessage(
                    message = stringResource(R.string.requests_load_error),
                    action = stringResource(R.string.requests_retry),
                    onAction = { onIntent(RequestsUIIntent.Retry) },
                )

                state.visibleRequests.isEmpty() -> RequestsMessage(message = stringResource(R.string.requests_active_empty))
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    itemsIndexed(
                        state.visibleRequests,
                        key = { _, item -> item.stableKey }) { index, item ->
                        PharmacyWorkCard(
                            item = item,
                            onClick = { onIntent(RequestsUIIntent.RequestClicked(item.id)) })
                        if (index == state.visibleRequests.lastIndex && state.canLoadMore) {
                            LaunchedEffect(item.stableKey, state.selectedFilter) {
                                onIntent(RequestsUIIntent.LoadMore)
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
private fun RequestsFiltersRow(
    selected: RequestsFilter,
    onSelected: (RequestsFilter) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        RequestsFilter.entries.forEach { filter ->
            FilterChip(
                selected = selected == filter,
                onClick = { onSelected(filter) },
                label = { Text(stringResource(filter.labelRes())) },
            )
        }
    }
}

private fun RequestsFilter.labelRes(): Int = when (this) {
    RequestsFilter.All -> R.string.requests_filter_all
    RequestsFilter.Pending -> R.string.requests_filter_pending
    RequestsFilter.OfferCreated -> R.string.requests_filter_offer_created
    RequestsFilter.Expired -> R.string.requests_filter_expired
}

@Composable
private fun RequestsMessage(
    message: String,
    action: String? = null,
    onAction: () -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        MedsyLottie(com.medsy.designsystem.R.raw.no_data_found, modifier = Modifier.size(180.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp),
        )
        action?.let {
            MedsyButton(onClick = onAction, modifier = Modifier.padding(top = 16.dp)) { Text(it) }
        }
    }
}
