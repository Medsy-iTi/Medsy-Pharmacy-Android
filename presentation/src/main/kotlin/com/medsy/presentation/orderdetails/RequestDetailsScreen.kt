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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.designsystem.components.MedsySnackbarHost
import com.medsy.designsystem.components.showSuccess
import com.medsy.presentation.orderdetails.components.CustomerNotesSection
import com.medsy.presentation.orderdetails.components.OrderActionButtons
import com.medsy.presentation.orderdetails.components.OrderDetailsTopBar
import com.medsy.presentation.orderdetails.components.OrderInfoCard
import com.medsy.presentation.orderdetails.components.OrderTotalSummaryRow
import com.medsy.presentation.orderdetails.components.PaymentMethodSection
import com.medsy.presentation.orderdetails.components.PharmacistNotesSection
import com.medsy.presentation.orderdetails.components.PrescriptionImageSection
import com.medsy.presentation.orderdetails.components.RequestedMedicinesSection

@Composable
fun RequestDetailsRoot(
    requestId: Long,
    onNavigateBack: () -> Unit,
    onDialPhoneNumber: (String) -> Unit,
    onOpenLocationOnMap: () -> Unit,
    onOpenPaymentSummary: () -> Unit,
    onOpenCustomerChat: () -> Unit,
    viewModel: RequestDetailsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(requestId) {
        viewModel.onIntent(RequestDetailsUIIntent.LoadRequest(requestId))
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                RequestDetailsUIEffect.NavigateBack -> onNavigateBack()
                is RequestDetailsUIEffect.DialPhoneNumber -> onDialPhoneNumber(effect.phoneNumber)
                RequestDetailsUIEffect.OpenLocationOnMap -> onOpenLocationOnMap()
                RequestDetailsUIEffect.OpenPaymentSummary -> onOpenPaymentSummary()
                RequestDetailsUIEffect.OpenCustomerChat -> onOpenCustomerChat()
                is RequestDetailsUIEffect.ShowMessage ->
                    snackbarHostState.showSuccess(context.getString(effect.messageRes))
            }
        }
    }

    RequestDetailsScreen(
        state = state,
        onIntent = viewModel::onIntent,
        snackbarHostState = snackbarHostState,
    )
}


@Composable
fun RequestDetailsScreen(
    state: RequestDetailsUIState,
    onIntent: (RequestDetailsUIIntent) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { MedsySnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        if (state.isLoading || state.request == null) {
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

        val request = state.request

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            OrderDetailsTopBar(
                isNewOrder = request.isNew,
                onBackClick = { onIntent(RequestDetailsUIIntent.BackClicked) },
                modifier = Modifier.padding(horizontal = 12.dp),
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
            ) {
                OrderInfoCard(
                    orderId = request.id,
                    minutesAgo = request.minutesAgo,
                    customerName = request.customerName,
                    customerPhone = request.customerPhone,
                    customerAddress = request.customerAddress,
                    onCallClick = { onIntent(RequestDetailsUIIntent.CallCustomerClicked) },
                    onLocationClick = { onIntent(RequestDetailsUIIntent.OpenLocationClicked) },
                    modifier = Modifier.padding(top = 16.dp),
                )

                RequestedMedicinesSection(
                    items = request.items,
                    modifier = Modifier.padding(top = 24.dp),
                )

                PrescriptionImageSection(
                    imageUrl = request.prescriptionUrl,
                    onImageClick = {},
                    modifier = Modifier.padding(top = 24.dp),
                )

                CustomerNotesSection(
                    notes = request.customerNotes,
                    modifier = Modifier.padding(top = 24.dp),
                )

                PharmacistNotesSection(
                    notes = state.pharmacistNotes,
                    onNotesChanged = { newNotes ->
                        onIntent(RequestDetailsUIIntent.PharmacistNotesChanged(newNotes))
                    },
                    modifier = Modifier.padding(top = 24.dp),
                )

                PaymentMethodSection(
                    paymentMethod = request.paymentMethod,
                    modifier = Modifier.padding(top = 24.dp),
                )

                OrderTotalSummaryRow(
                    total = request.total,
                    modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
                )
            }

            OrderActionButtons(
                isSubmitting = state.isSubmitting,
                onRejectClick = { onIntent(RequestDetailsUIIntent.RejectRequestClicked) },
                onContactClick = { onIntent(RequestDetailsUIIntent.ContactCustomerClicked) },
                onAcceptClick = { onIntent(RequestDetailsUIIntent.AcceptRequestClicked) },
            )
        }
    }
}
