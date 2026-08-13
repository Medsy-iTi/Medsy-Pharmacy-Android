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
import com.medsy.presentation.worklist.components.PharmacyOrderCard
import com.medsy.presentation.worklist.components.RequestsSearchBar
import com.medsy.presentation.worklist.components.RequestsShimmer

@Composable
fun OrdersRoot(
    onOrderClick: (Long) -> Unit,
    viewModel: OrdersViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.onIntent(OrdersUIIntent.Refresh)
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is OrdersUIEffect.NavigateToOrderDetails -> onOrderClick(effect.orderId)
            }
        }
    }
    OrdersScreen(state, viewModel::onIntent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(state: OrdersUIState, onIntent: (OrdersUIIntent) -> Unit, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Text(
                stringResource(R.string.orders_title),
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
            onRefresh = { onIntent(OrdersUIIntent.Refresh) },
            modifier = Modifier.fillMaxSize().padding(paddingValues),
        ) {
            Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                OrdersFiltersRow(state.selectedFilter) { onIntent(OrdersUIIntent.FilterSelected(it)) }
                RequestsSearchBar(
                    state.searchQuery,
                    { onIntent(OrdersUIIntent.SearchQueryChanged(it)) },
                    Modifier.padding(top = 12.dp),
                )
                when {
                    state.isLoading -> RequestsShimmer()
                    state.hasError -> OrdersMessage(
                        stringResource(R.string.orders_load_error),
                        stringResource(R.string.requests_retry),
                    ) { onIntent(OrdersUIIntent.Retry) }
                    state.visibleOrders.isEmpty() -> OrdersMessage(stringResource(R.string.orders_empty))
                    else -> LazyColumn(
                        Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        itemsIndexed(state.visibleOrders, key = { _, item -> item.id }) { index, item ->
                            PharmacyOrderCard(
                                order = item,
                                onClick = { onIntent(OrdersUIIntent.OrderClicked(item.id)) },
                            )
                            if (index == state.visibleOrders.lastIndex && state.canLoadMore) {
                                LaunchedEffect(item.id) { onIntent(OrdersUIIntent.LoadMore) }
                            }
                        }
                        if (state.isLoadingMore) item { CircularProgressIndicator(Modifier.padding(16.dp).size(32.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrdersFiltersRow(selected: OrdersFilter, onSelected: (OrdersFilter) -> Unit) {
    Row(
        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OrdersFilter.entries.forEach { filter ->
            FilterChip(selected == filter, { onSelected(filter) }, label = { Text(stringResource(filter.labelRes())) })
        }
    }
}

private fun OrdersFilter.labelRes(): Int = when (this) {
    OrdersFilter.All -> R.string.orders_filter_all
    OrdersFilter.WaitingForPatient -> R.string.orders_filter_waiting_for_patient
    OrdersFilter.InProgress -> R.string.orders_filter_in_progress
    OrdersFilter.Completed -> R.string.orders_filter_completed
}

@Composable
private fun OrdersMessage(message: String, action: String? = null, onAction: () -> Unit = {}) {
    Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
        MedsyLottie(com.medsy.designsystem.R.raw.no_data_found, Modifier.size(180.dp))
        Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        action?.let { MedsyButton(onClick = onAction, modifier = Modifier.padding(top = 16.dp)) { Text(it) } }
    }
}
