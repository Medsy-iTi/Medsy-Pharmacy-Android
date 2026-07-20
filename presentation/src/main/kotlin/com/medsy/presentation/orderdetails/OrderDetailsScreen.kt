package com.medsy.presentation.orderdetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.designsystem.components.MedsySnackbarHost
import com.medsy.designsystem.components.showSuccess
import com.medsy.designsystem.ui.theme.MedsyTheme
import com.medsy.presentation.orderdetails.components.CustomerNotesSection
import com.medsy.presentation.orderdetails.components.OrderActionButtons
import com.medsy.presentation.orderdetails.components.OrderDetailsTopBar
import com.medsy.presentation.orderdetails.components.OrderInfoCard
import com.medsy.presentation.orderdetails.components.OrderTotalSummaryRow
import com.medsy.presentation.orderdetails.components.RequestedMedicinesSection

@Composable
fun OrderDetailsRoot(
    orderId: String,
    onNavigateBack: () -> Unit,
    onDialPhoneNumber: (String) -> Unit,
    onOpenLocationOnMap: () -> Unit,
    onOpenPaymentSummary: () -> Unit,
    onOpenCustomerChat: () -> Unit,
    viewModel: OrderDetailsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(orderId) {
        viewModel.onIntent(OrderDetailsUIIntent.LoadOrder(orderId))
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                OrderDetailsUIEffect.NavigateBack -> onNavigateBack()
                is OrderDetailsUIEffect.DialPhoneNumber -> onDialPhoneNumber(effect.phoneNumber)
                OrderDetailsUIEffect.OpenLocationOnMap -> onOpenLocationOnMap()
                OrderDetailsUIEffect.OpenPaymentSummary -> onOpenPaymentSummary()
                OrderDetailsUIEffect.OpenCustomerChat -> onOpenCustomerChat()
                is OrderDetailsUIEffect.ShowMessage ->
                    snackbarHostState.showSuccess(context.getString(effect.messageRes))
            }
        }
    }

    OrderDetailsScreen(
        state = state,
        onIntent = viewModel::onIntent,
        snackbarHostState = snackbarHostState,
    )
}


@Composable
fun OrderDetailsScreen(
    state: OrderDetailsUIState,
    onIntent: (OrderDetailsUIIntent) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { MedsySnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        if (state.isLoading || state.order == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        val order = state.order

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            OrderDetailsTopBar(
                isNewOrder = order.isNew,
                onBackClick = { onIntent(OrderDetailsUIIntent.BackClicked) },
                modifier = Modifier.padding(horizontal = 12.dp),
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
            ) {
                OrderInfoCard(
                    orderId = order.id,
                    minutesAgo = order.minutesAgo,
                    customerName = order.customerName,
                    customerPhone = order.customerPhone,
                    customerAddress = order.customerAddress,
                    onCallClick = { onIntent(OrderDetailsUIIntent.CallCustomerClicked) },
                    onLocationClick = { onIntent(OrderDetailsUIIntent.OpenLocationClicked) },
                    modifier = Modifier.padding(top = 16.dp),
                )

                RequestedMedicinesSection(
                    items = order.items,
                    modifier = Modifier.padding(top = 24.dp),
                )

                if (!order.customerNotes.isNullOrBlank()) {
                    CustomerNotesSection(
                        notes = order.customerNotes,
                        modifier = Modifier.padding(top = 24.dp),
                    )
                }

                OrderTotalSummaryRow(
                    total = order.total,
                    onViewSummaryClick = {
                        onIntent(OrderDetailsUIIntent.ViewPaymentSummaryClicked)
                    },
                    modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
                )
            }

            OrderActionButtons(
                isSubmitting = state.isSubmitting,
                onRejectClick = { onIntent(OrderDetailsUIIntent.RejectOrderClicked) },
                onContactClick = { onIntent(OrderDetailsUIIntent.ContactCustomerClicked) },
                onAcceptClick = { onIntent(OrderDetailsUIIntent.AcceptOrderClicked) },
            )
        }
    }
}
