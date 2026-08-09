package com.medsy.presentation.orders.orderdetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.designsystem.components.MedsyButton
import com.medsy.presentation.R
import com.medsy.presentation.orderdetails.components.CustomerNotesSection
import com.medsy.presentation.orderdetails.components.OrderStatusTimeline
import com.medsy.presentation.orderdetails.components.PaymentMethodSection
import com.medsy.presentation.orderdetails.components.PrescriptionImageSection
import com.medsy.presentation.orderdetails.components.RequestDetailsShimmer
import com.medsy.presentation.orderdetails.components.RequestDetailsTopBar
import com.medsy.presentation.orderdetails.components.RequestInfoCard
import com.medsy.presentation.orderdetails.components.RequestTotalSummaryRow
import com.medsy.presentation.orderdetails.components.RequestedMedicinesSection
import com.medsy.presentation.orderdetails.components.ZoomableImageDialog

@Composable
fun OrderDetailsRoot(
    orderId: Long,
    onNavigateBack: () -> Unit,
    onDialPhoneNumber: (String) -> Unit,
    onOpenLocationOnMap: (Double, Double) -> Unit,
    viewModel: OrderDetailsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(orderId) { viewModel.onIntent(OrderDetailsUIIntent.Load(orderId)) }
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                OrderDetailsUIEffect.NavigateBack -> onNavigateBack()
                is OrderDetailsUIEffect.DialPhoneNumber -> onDialPhoneNumber(effect.phoneNumber)
                is OrderDetailsUIEffect.OpenLocation -> onOpenLocationOnMap(
                    effect.latitude,
                    effect.longitude
                )
            }
        }
    }
    OrderDetailsScreen(state = state, onIntent = viewModel::onIntent)
}

@Composable
fun OrderDetailsScreen(
    state: OrderDetailsUIState,
    onIntent: (OrderDetailsUIIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        when {
            state.isLoading -> Box(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) { RequestDetailsShimmer() }

            state.hasError || state.order == null -> Box(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues), contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(R.string.order_details_load_error))
                    MedsyButton(
                        onClick = { onIntent(OrderDetailsUIIntent.Retry) },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text(stringResource(R.string.requests_retry))
                    }
                }
            }

            else -> {
                val order = state.order
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    RequestDetailsTopBar(
                        isNewOrder = false,
                        onBackClick = { onIntent(OrderDetailsUIIntent.BackClicked) },
                        titleRes = R.string.order_details_title,
                        modifier = Modifier.padding(horizontal = 12.dp),
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                    ) {
                        OrderStatusTimeline(state.status, Modifier.padding(top = 16.dp))
                        RequestInfoCard(
                            orderId = order.id,
                            minutesAgo = state.minutesAgo,
                            customerName = order.customerName ?: stringResource(
                                R.string.customer_default_format,
                                order.customerId
                            ),
                            customerPhone = order.customerPhone,
                            customerAddress = order.customerAddress,
                            onCallClick = { onIntent(OrderDetailsUIIntent.CallCustomerClicked) },
                            onLocationClick = { onIntent(OrderDetailsUIIntent.OpenLocationClicked) },
                            modifier = Modifier.padding(top = 16.dp),
                        )
                        RequestedMedicinesSection(
                            items = order.items,
                            selectedItems = emptySet(),
                            onItemCheckedChange = {},
                            onAddSubstituteClick = {},
                            readOnly = true,
                            modifier = Modifier.padding(top = 24.dp),
                        )
                        var imageDialog by remember { mutableStateOf<String?>(null) }
                        PrescriptionImageSection(
                            imageUrl = order.prescriptionUrl,
                            onImageClick = { order.prescriptionUrl?.let { imageDialog = it } },
                            modifier = Modifier.padding(top = 24.dp),
                        )
                        imageDialog?.let { image ->
                            ZoomableImageDialog(
                                image,
                                onDismissRequest = { imageDialog = null })
                        }
                        order.customerNotes?.takeIf(String::isNotBlank)?.let {
                            CustomerNotesSection(it, Modifier.padding(top = 24.dp))
                        }
                        PaymentMethodSection(order.paymentMethod, Modifier.padding(top = 24.dp))
                        RequestTotalSummaryRow(
                            order.total,
                            Modifier.padding(top = 24.dp, bottom = 24.dp)
                        )
                    }
                }
            }
        }
    }
}
