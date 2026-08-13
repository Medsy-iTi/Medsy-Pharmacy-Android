package com.medsy.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsy.designsystem.components.MedsyButton
import com.medsy.designsystem.ui.theme.extendedColors
import com.medsy.presentation.R
import com.medsy.presentation.home.components.HomeShimmer
import com.medsy.presentation.home.components.LatestOrdersSection
import com.medsy.presentation.home.components.OverviewSection
import com.medsy.presentation.home.components.PharmacyMainCard

@Composable
fun HomeRoot(
    onOpenNotifications: () -> Unit,
    onViewAllOrders: () -> Unit,
    onOrderClick: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                HomeUIEffect.NavigateToViewAllOrders -> onViewAllOrders()
                is HomeUIEffect.NavigateToOrderDetails -> onOrderClick(effect.orderId)
                HomeUIEffect.OpenNotifications -> onOpenNotifications()
            }
        }
    }
    HomeScreen(state, viewModel::onIntent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(state: HomeUIState, onIntent: (HomeUIIntent) -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.home_role_pharmacist),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.extendedColors.darkBlueColor,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onIntent(HomeUIIntent.OnNotificationsClicked) }) {
                        BadgedBox(
                            badge = {
                                if (state.notificationsCount > 0) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.error,
                                        contentColor = MaterialTheme.colorScheme.onError,
                                    ) { Text(state.notificationsCount.toString()) }
                                }
                            },
                        ) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = stringResource(R.string.home_notifications_description),
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                windowInsets = WindowInsets(0.dp),
            )
        },
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { onIntent(HomeUIIntent.Refresh) },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                if (state.isLoading && state.pharmacy == null) {
                    HomeShimmer()
                } else if (state.errorRes != null && state.pharmacy == null) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            stringResource(state.errorRes),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                        MedsyButton(
                            onClick = { onIntent(HomeUIIntent.Refresh) },
                            modifier = Modifier.padding(top = 16.dp),
                        ) { Text(stringResource(R.string.requests_retry)) }
                    }
                } else {
                    LazyColumn(
                        Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        verticalArrangement = Arrangement.Top,
                    ) {
                        state.pharmacy?.let { pharmacy -> item { PharmacyMainCard(pharmacy) } }
                        state.dashboard?.let { dashboard ->
                            item {
                                Spacer(Modifier.height(12.dp))
                                OverviewSection(dashboard)
                            }
                        }
                        item {
                            Spacer(Modifier.height(16.dp))
                            LatestOrdersSection(state.latestOrders) {
                                onIntent(
                                    HomeUIIntent.OnOrderClicked(
                                        it
                                    )
                                )
                            }
                        }
                        item { Spacer(Modifier.height(24.dp)) }
                    }
                }
            }
        }
    }
}
