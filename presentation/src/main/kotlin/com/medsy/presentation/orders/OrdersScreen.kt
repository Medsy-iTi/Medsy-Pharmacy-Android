package com.medsy.presentation.orders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.designsystem.components.MedsySnackbarHost
import com.medsy.designsystem.components.showSuccess
import com.medsy.designsystem.ui.theme.MedsyTheme
import com.medsy.designsystem.ui.theme.extendedColors
import com.medsy.presentation.R
import com.medsy.presentation.orders.components.OrderCard
import com.medsy.presentation.orders.components.OrdersFilterChipsRow
import com.medsy.presentation.orders.components.OrdersSearchBar

@Composable
fun OrdersRoot(
    onOrderClick: (String) -> Unit,
    viewModel: OrdersViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is OrdersUIEffect.NavigateToOrderDetails -> onOrderClick(effect.orderId)
                OrdersUIEffect.OpenFilters -> { /* TODO: open a filters bottom sheet once designed */ }
                is OrdersUIEffect.ShowMessage ->
                    snackbarHostState.showSuccess(context.getString(effect.messageRes))
            }
        }
    }

    OrdersScreen(
        state = state,
        onIntent = viewModel::onIntent,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun OrdersScreen(
    state: OrdersUIState,
    onIntent: (OrdersUIIntent) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { MedsySnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Text(
                text = stringResource(R.string.orders_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.extendedColors.darkBlueColor,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
        ) {
            OrdersFilterChipsRow(
                selectedFilter = state.selectedFilter,
                newCount = state.newCount,
                inProgressCount = state.inProgressCount,
                onFilterSelected = { onIntent(OrdersUIIntent.FilterSelected(it)) },
            )

            OrdersSearchBar(
                query = state.searchQuery,
                onQueryChange = { onIntent(OrdersUIIntent.SearchQueryChanged(it)) },
                onFilterClick = { onIntent(OrdersUIIntent.FilterIconClicked) },
                modifier = Modifier.padding(top = 12.dp),
            )

            when {
                state.isLoading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }

                state.filteredOrders.isEmpty() -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.orders_active_empty),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(state.filteredOrders, key = { it.id }) { order ->
                        OrderCard(
                            order = order,
                            onCardClick = { onIntent(OrdersUIIntent.OrderClicked(order.id)) },
                            onAcceptClick = { onIntent(OrdersUIIntent.AcceptOrderClicked(order.id)) },
                            onPrepareClick = { onIntent(OrdersUIIntent.PrepareOrderClicked(order.id)) },
                        )
                    }
                }
            }
        }
    }
}