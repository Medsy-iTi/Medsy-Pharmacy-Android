package com.medsy.presentation.requests

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.designsystem.components.MedsyButton
import com.medsy.designsystem.components.MedsyLottie
import com.medsy.designsystem.ui.theme.extendedColors
import com.medsy.presentation.R
import com.medsy.presentation.worklist.components.PharmacyRequestCard
import com.medsy.presentation.worklist.components.RequestsSearchBar
import com.medsy.presentation.worklist.components.RequestsShimmer

@Composable
fun RequestsRoot(
    onRequestClick: (Long) -> Unit,
    viewModel: RequestsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.onIntent(RequestsUIIntent.Refresh)
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is RequestsUIEffect.NavigateToRequestDetails -> onRequestClick(effect.requestId)
            }
        }
    }
    RequestsScreen(state, viewModel::onIntent)
}

@OptIn(ExperimentalMaterial3Api::class)
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
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            )
        },
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { onIntent(RequestsUIIntent.Refresh) },
            modifier = Modifier.fillMaxSize().padding(paddingValues),
        ) {
            Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                RequestsFiltersRow(state.selectedFilter) { onIntent(RequestsUIIntent.FilterSelected(it)) }
                RequestsSearchBar(
                    query = state.searchQuery,
                    onQueryChange = { onIntent(RequestsUIIntent.SearchQueryChanged(it)) },
                    modifier = Modifier.padding(top = 12.dp),
                )
                when {
                    state.isLoading -> RequestsShimmer()
                    state.hasError -> RequestsMessage(
                        stringResource(R.string.requests_load_error),
                        stringResource(R.string.requests_retry),
                    ) { onIntent(RequestsUIIntent.Retry) }
                    state.visibleRequests.isEmpty() -> RequestsMessage(stringResource(R.string.requests_active_empty))
                    else -> LazyColumn(
                        Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        itemsIndexed(state.visibleRequests, key = { _, item -> item.id }) { index, item ->
                            PharmacyRequestCard(
                                request = item,
                                onClick = { onIntent(RequestsUIIntent.RequestClicked(item.id)) },
                            )
                            if (index == state.visibleRequests.lastIndex && state.canLoadMore) {
                                LaunchedEffect(item.id, state.selectedFilter) { onIntent(RequestsUIIntent.LoadMore) }
                            }
                        }
                        if (state.isLoadingMore) item {
                            CircularProgressIndicator(Modifier.padding(16.dp).size(32.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RequestsFiltersRow(selected: RequestsFilter, onSelected: (RequestsFilter) -> Unit) {
    Row(
        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        RequestsFilter.entries.forEach { filter ->
            FilterChip(selected == filter, { onSelected(filter) }, label = { Text(stringResource(filter.labelRes())) })
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
private fun RequestsMessage(message: String, action: String? = null, onAction: () -> Unit = {}) {
    Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
        MedsyLottie(com.medsy.designsystem.R.raw.no_data_found, Modifier.size(180.dp))
        Text(
            message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp),
        )
        action?.let { MedsyButton(onClick = onAction, modifier = Modifier.padding(top = 16.dp)) { Text(it) } }
    }
}
