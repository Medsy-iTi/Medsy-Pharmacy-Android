package com.medsy.presentation.orders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.designsystem.components.MedsyLottie
import com.medsy.designsystem.components.MedsySnackbarHost
import com.medsy.designsystem.components.showSuccess
import com.medsy.designsystem.ui.theme.extendedColors
import com.medsy.presentation.R
import com.medsy.presentation.orders.components.RequestCard
import com.medsy.presentation.orders.components.RequestsFilterChipsRow
import com.medsy.presentation.orders.components.RequestsSearchBar

@Composable
fun RequestsRoot(
    onRequestClick: (Long) -> Unit,
    viewModel: RequestsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is RequestsUIEffect.NavigateToRequestDetails -> onRequestClick(effect.requestId)
                RequestsUIEffect.OpenFilters -> {  }
                is RequestsUIEffect.ShowMessage ->
                    snackbarHostState.showSuccess(context.getString(effect.messageRes))
            }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                viewModel.onIntent(RequestsUIIntent.Refresh)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    RequestsScreen(
        state = state,
        onIntent = viewModel::onIntent,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun RequestsScreen(
    state: RequestsUIState,
    onIntent: (RequestsUIIntent) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { MedsySnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Text(
                text = stringResource(R.string.requests_title),
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
            RequestsFilterChipsRow(
                selectedFilter = state.selectedFilter,
                newCount = state.newCount,
                inProgressCount = state.inProgressCount,
                onFilterSelected = { onIntent(RequestsUIIntent.FilterSelected(it)) },
            )

            RequestsSearchBar(
                query = state.searchQuery,
                onQueryChange = { onIntent(RequestsUIIntent.SearchQueryChanged(it)) },
                onFilterClick = { onIntent(RequestsUIIntent.FilterIconClicked) },
                modifier = Modifier.padding(top = 12.dp),
            )

            when {
                state.isLoading -> com.medsy.presentation.orders.components.RequestsShimmer(
                    modifier = Modifier.fillMaxSize()
                )

                state.filteredOrders.isEmpty() -> Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    MedsyLottie(
                        resId = com.medsy.designsystem.R.raw.no_data_found,
                        modifier = Modifier.size(200.dp)
                    )
                    Text(
                        text = stringResource(R.string.requests_active_empty),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(state.filteredOrders, key = { it.id }) { request ->
                        RequestCard(
                            request = request,
                            onCardClick = { onIntent(RequestsUIIntent.RequestClicked(request.id)) },
                            onAcceptClick = { onIntent(RequestsUIIntent.AcceptRequestClicked(request.id)) },
                            onPrepareClick = { onIntent(RequestsUIIntent.PrepareRequestClicked(request.id)) },
                        )
                    }
                }
            }
        }
    }
}
