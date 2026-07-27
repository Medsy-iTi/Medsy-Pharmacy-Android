package com.medsy.presentation.orderdetails

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.designsystem.components.MedsyLottie
import com.medsy.designsystem.ui.theme.extendedColors
import com.medsy.domain.products.model.Product
import com.medsy.presentation.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubstituteSearchRoot(
    requestItemId: Long,
    onNavigateBack: () -> Unit,
    viewModel: SubstituteSearchViewModel = hiltViewModel(),
    resultManager: SubstituteResultManager = hiltViewModel<SubstituteResultViewModel>().manager
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                SubstituteSearchUIEffect.NavigateBack -> onNavigateBack()
                is SubstituteSearchUIEffect.ReturnSubstitute -> {
                    resultManager.sendResult(
                        SubstituteResult(
                            requestItemId = requestItemId,
                            productId = effect.productId,
                            productName = effect.productName,
                            productPrice = effect.productPrice,
                            productImage = effect.productImage
                        )
                    )
                    onNavigateBack()
                }
            }
        }
    }

    SubstituteSearchScreen(state = state, onIntent = viewModel::onIntent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubstituteSearchScreen(
    state: SubstituteSearchUIState,
    onIntent: (SubstituteSearchUIIntent) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.substitute_search_title)) },
                navigationIcon = {
                    // Just a back button
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            OutlinedTextField(
                value = state.query,
                onValueChange = { onIntent(SubstituteSearchUIIntent.SearchQueryChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text(stringResource(R.string.substitute_search_placeholder)) }
            )

            if (state.isLoading) {
                com.medsy.designsystem.components.MedsyShimmer(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        repeat(5) {
                            com.medsy.designsystem.components.MedsyShimmerPlaceholder(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(72.dp)
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                            )
                        }
                    }
                }
            } else if (state.products.isEmpty() && state.query.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        MedsyLottie(
                            resId = com.medsy.designsystem.R.raw.no_data_found,
                            modifier = Modifier.size(200.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.substitute_search_lottie_desc),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.extendedColors.neutralContent
                        )
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.products, key = { it.id }) { product ->
                        ProductItemRow(product = product, onClick = {
                            onIntent(SubstituteSearchUIIntent.ProductSelected(product.id, product.name, product.price, product.imageUrl))
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun ProductItemRow(product: Product, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(product.name, style = MaterialTheme.typography.bodyLarge)
            Text("${product.packSize ?: ""} - ${product.form ?: ""}", style = MaterialTheme.typography.bodySmall)
        }
        Text(stringResource(R.string.substitute_price_egp, product.price), style = MaterialTheme.typography.bodyMedium)
    }
}
