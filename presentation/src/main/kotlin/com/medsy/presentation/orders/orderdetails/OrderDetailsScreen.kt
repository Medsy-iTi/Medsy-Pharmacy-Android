package com.medsy.presentation.orders.orderdetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.designsystem.components.MedsyButton
import com.medsy.designsystem.components.MedsySnackbarHost
import com.medsy.designsystem.components.showSuccess
import com.medsy.presentation.R
import com.medsy.presentation.orderdetails.components.RequestDetailsShimmer

@Composable
fun OrderDetailsRoot(
    orderId: Long,
    onNavigateBack: () -> Unit,
    onDialPhoneNumber: (String) -> Unit,
    onOpenLocationOnMap: (Double, Double) -> Unit,
    viewModel: OrderDetailsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(orderId) { viewModel.onIntent(OrderDetailsUIIntent.Load(orderId)) }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.onIntent(OrderDetailsUIIntent.Refresh)
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                OrderDetailsUIEffect.NavigateBack -> onNavigateBack()
                is OrderDetailsUIEffect.DialPhoneNumber -> onDialPhoneNumber(effect.phoneNumber)
                is OrderDetailsUIEffect.OpenLocation -> onOpenLocationOnMap(effect.latitude, effect.longitude)
                is OrderDetailsUIEffect.ShowMessage -> snackbarHostState.showSuccess(
                    ContextCompat.getString(context, effect.messageRes)
                )
            }
        }
    }
    OrderDetailsScreen(state, viewModel::onIntent, snackbarHostState)
}

@Composable
fun OrderDetailsScreen(
    state: OrderDetailsUIState,
    onIntent: (OrderDetailsUIIntent) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val order = state.order
    if (order != null) {
        Box(Modifier.fillMaxSize()) {
            OrderDetailsContent(
                order = order,
                isRefreshing = state.isRefreshing,
                isUpdatingStatus = state.isUpdatingStatus,
                isStatusActionBlocked = state.isStatusActionBlocked,
                showStatusConfirmation = state.showStatusConfirmation,
                onBack = { onIntent(OrderDetailsUIIntent.BackClicked) },
                onRefresh = { onIntent(OrderDetailsUIIntent.Refresh) },
                onCall = { onIntent(OrderDetailsUIIntent.CallCustomerClicked) },
                onLocation = { onIntent(OrderDetailsUIIntent.OpenLocationClicked) },
                onStatusAction = { onIntent(OrderDetailsUIIntent.StatusActionClicked) },
                onConfirmStatus = { onIntent(OrderDetailsUIIntent.ConfirmStatusAction) },
                onDismissStatus = { onIntent(OrderDetailsUIIntent.DismissStatusConfirmation) },
            )
            MedsySnackbarHost(snackbarHostState, Modifier.align(Alignment.BottomCenter))
        }
        return
    }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when {
            state.isLoading -> RequestDetailsShimmer()
            state.hasError -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(R.string.order_details_load_error), color = MaterialTheme.colorScheme.onSurfaceVariant)
                MedsyButton(
                    onClick = { onIntent(OrderDetailsUIIntent.Retry) },
                    modifier = Modifier.padding(top = 16.dp),
                ) { Text(stringResource(R.string.requests_retry)) }
            }
        }
    }
}
