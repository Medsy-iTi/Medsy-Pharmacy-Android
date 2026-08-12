package com.medsy.presentation.orderdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.designsystem.components.MedsySnackbarHost
import com.medsy.designsystem.components.showSuccess
import kotlinx.coroutines.launch
import com.medsy.presentation.orderdetails.components.CustomerNotesSection
import com.medsy.presentation.orderdetails.components.RequestActionButtons
import com.medsy.presentation.orderdetails.components.RequestAssignmentStatusCard
import com.medsy.presentation.orderdetails.components.RequestDetailsTopBar
import com.medsy.presentation.orderdetails.components.RequestInfoCard
import com.medsy.presentation.orderdetails.components.RequestTotalSummaryRow
import com.medsy.presentation.orderdetails.components.PaymentMethodSection
import com.medsy.presentation.orderdetails.components.PrescriptionImageSection
import com.medsy.presentation.orderdetails.components.RequestedMedicinesSection
import com.medsy.presentation.orderdetails.components.ZoomableImageDialog
import com.medsy.presentation.R
import com.medsy.presentation.orderdetails.components.RequestDetailsShimmer

@Composable
fun RequestDetailsRoot(
    requestId: Long,
    onNavigateBack: () -> Unit,
    onDialPhoneNumber: (String) -> Unit,
    onOpenLocationOnMap: (Double, Double) -> Unit,
    onOpenPaymentSummary: () -> Unit,
    onNavigateToSubstituteSearch: (Long) -> Unit,
    onOpenPrescriptionImage: (String) -> Unit,
    viewModel: RequestDetailsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(requestId) {
        viewModel.onIntent(RequestDetailsUIIntent.LoadRequest(requestId))
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                RequestDetailsUIEffect.NavigateBack -> onNavigateBack()
                is RequestDetailsUIEffect.DialPhoneNumber -> onDialPhoneNumber(effect.phoneNumber)
                is RequestDetailsUIEffect.OpenLocationOnMap -> onOpenLocationOnMap(effect.latitude, effect.longitude)
                RequestDetailsUIEffect.OpenPaymentSummary -> onOpenPaymentSummary()
                is RequestDetailsUIEffect.NavigateToSubstituteSearch -> onNavigateToSubstituteSearch(effect.itemId)
                is RequestDetailsUIEffect.ShowMessage -> {
                    coroutineScope.launch { snackbarHostState.showSuccess(context.getString(effect.messageRes)) }
                }
                is RequestDetailsUIEffect.OpenPrescriptionImage -> onOpenPrescriptionImage(effect.imageUrl)
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
        val request = state.request

        if (request == null) {
            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                ) {
                    RequestDetailsShimmer()
                }
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            RequestDetailsTopBar(
                isNewOrder = request.isNew && state.canCreateOffer,
                onBackClick = { onIntent(RequestDetailsUIIntent.BackClicked) },
                modifier = Modifier.padding(horizontal = 12.dp),
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
            ) {
                if (!state.canCreateOffer) {
                    RequestAssignmentStatusCard(
                        status = state.assignmentStatus,
                        modifier = Modifier.padding(top = 16.dp),
                    )
                }

                RequestInfoCard(
                    orderId = request.id,
                    minutesAgo = request.minutesAgo,
                    customerName = request.customerName ?: stringResource(R.string.customer_default_format, request.customerId),
                    customerPhone = request.customerPhone,
                    customerAddress = request.customerAddress,
                    onCallClick = { onIntent(RequestDetailsUIIntent.CallCustomerClicked) },
                    onLocationClick = { onIntent(RequestDetailsUIIntent.OpenLocationClicked) },
                    modifier = Modifier.padding(top = 16.dp),
                )

                RequestedMedicinesSection(
                    items = request.items,
                    selectedItems = state.selectedItems,
                    onItemCheckedChange = { itemId ->
                        onIntent(RequestDetailsUIIntent.ToggleItemSelection(itemId))
                    },
                    onAddSubstituteClick = { itemId ->
                        onIntent(RequestDetailsUIIntent.AddSubstituteClicked(itemId))
                    },
                    readOnly = !state.canCreateOffer,
                    modifier = Modifier.padding(top = 24.dp),
                )

                var showImageDialog by remember { mutableStateOf<String?>(null) }

                PrescriptionImageSection(
                    imageUrl = request.prescriptionUrl,
                    onImageClick = { request.prescriptionUrl?.let { url -> showImageDialog = url } },
                    modifier = Modifier.padding(top = 24.dp),
                )

                if (showImageDialog != null) {
                    ZoomableImageDialog(
                        imageUrl = showImageDialog!!,
                        onDismissRequest = { showImageDialog = null }
                    )
                }

                if (!request.customerNotes.isNullOrEmpty()) {
                    CustomerNotesSection(
                        notes = request.customerNotes,
                        modifier = Modifier.padding(top = 24.dp)
                    )
                }

                PaymentMethodSection(
                    paymentMethod = request.paymentMethod,
                    modifier = Modifier.padding(top = 24.dp),
                )

                RequestTotalSummaryRow(
                    total = request.total,
                    modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
                )
            }

            if (state.canCreateOffer) {
                RequestActionButtons(
                    isSubmitting = state.isSubmitting,
                    onAcceptClick = { onIntent(RequestDetailsUIIntent.AcceptRequestClicked) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                )
            }
        }
    }
}
