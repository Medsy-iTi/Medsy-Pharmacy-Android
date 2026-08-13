package com.medsy.presentation.orderdetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.medsy.presentation.orderdetails.components.RequestDetailsContent
import com.medsy.presentation.orderdetails.components.RequestDetailsShimmer

@Composable
fun RequestDetailsRoot(
    requestId: Long,
    onNavigateBack: () -> Unit,
    onDialPhoneNumber: (String) -> Unit,
    onOpenLocationOnMap: (Double, Double) -> Unit,
    onNavigateToSubstituteSearch: (Long) -> Unit,
    viewModel: RequestDetailsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(requestId) { viewModel.onIntent(RequestDetailsUIIntent.LoadRequest(requestId)) }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.onIntent(RequestDetailsUIIntent.Refresh)
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                RequestDetailsUIEffect.NavigateBack -> onNavigateBack()
                is RequestDetailsUIEffect.DialPhoneNumber -> onDialPhoneNumber(effect.phoneNumber)
                is RequestDetailsUIEffect.OpenLocationOnMap -> onOpenLocationOnMap(
                    effect.latitude,
                    effect.longitude
                )

                is RequestDetailsUIEffect.NavigateToSubstituteSearch -> onNavigateToSubstituteSearch(
                    effect.itemId
                )

                is RequestDetailsUIEffect.ShowMessage -> snackbarHostState.showSuccess(
                    ContextCompat.getString(context, effect.messageRes)
                )
            }
        }
    }
    RequestDetailsScreen(state, viewModel::onIntent, snackbarHostState)
}

@Composable
fun RequestDetailsScreen(
    state: RequestDetailsUIState,
    onIntent: (RequestDetailsUIIntent) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val request = state.request
    if (request != null) {
        Box(Modifier.fillMaxSize()) {
            RequestDetailsContent(
                state = state,
                onBack = { onIntent(RequestDetailsUIIntent.BackClicked) },
                onRefresh = { onIntent(RequestDetailsUIIntent.Refresh) },
                onCall = { onIntent(RequestDetailsUIIntent.CallCustomerClicked) },
                onLocation = { onIntent(RequestDetailsUIIntent.OpenLocationClicked) },
                onToggleItem = { onIntent(RequestDetailsUIIntent.ToggleItemSelection(it)) },
                onAddSubstitute = { onIntent(RequestDetailsUIIntent.AddSubstituteClicked(it)) },
                onSendOffer = { onIntent(RequestDetailsUIIntent.SendOfferClicked) },
            )
            MedsySnackbarHost(snackbarHostState, Modifier.align(Alignment.BottomCenter))
        }
        return
    }
    Scaffold(snackbarHost = { MedsySnackbarHost(snackbarHostState) }) { padding ->
        when {
            state.isLoading -> Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) { RequestDetailsShimmer() }

            state.hasError -> Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        stringResource(R.string.request_details_load_error),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    MedsyButton(
                        onClick = { onIntent(RequestDetailsUIIntent.Retry) },
                        modifier = Modifier.padding(top = 16.dp),
                    ) { Text(stringResource(R.string.requests_retry)) }
                }
            }
        }
    }
}
