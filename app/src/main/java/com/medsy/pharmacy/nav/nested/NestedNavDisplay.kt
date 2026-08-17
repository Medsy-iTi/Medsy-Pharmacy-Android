package com.medsy.pharmacy.nav.nested

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.medsy.pharmacy.nav.root.NAVIGATION_DURATION_MILLIS
import com.medsy.pharmacy.nav.root.Route
import com.medsy.pharmacy.nav.root.pop
import com.medsy.pharmacy.nav.root.push
import com.medsy.pharmacy.nav.root.setRoot
import com.medsy.presentation.home.HomeRoot
import com.medsy.presentation.orders.OrdersRoot
import com.medsy.presentation.profile.ProfileRoot
import com.medsy.presentation.requests.RequestsRoot
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
fun NestedNavDisplay(
    navigateBack: () -> Unit,
    openLogin: () -> Unit,
    openRequestDetails: (Long) -> Unit,
    openOrderDetails: (Long) -> Unit,
    openInvitePharmacist: () -> Unit,
    openPharmacistsList: () -> Unit,
    openPersonalInfo: () -> Unit,
    openNotifications: () -> Unit,
    openAiChat: () -> Unit,
) {
    val backStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Route.NestedNav.Home::class, Route.NestedNav.Home.serializer())
                    subclass(Route.NestedNav.Requests::class, Route.NestedNav.Requests.serializer())
                    subclass(Route.NestedNav.Orders::class, Route.NestedNav.Orders.serializer())
                    subclass(Route.NestedNav.Profile::class, Route.NestedNav.Profile.serializer())
                }
            }
        },
        Route.NestedNav.Home,
    )

    Scaffold(
        bottomBar = {
            Column {

                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.background,
                    tonalElevation = 0.dp
                ) {
                    BottomBarDestination.entries.forEach { destination ->
                        val selected = backStack.lastOrNull() == destination.route
                        if (destination.isProminent) {
                            AiChatNavigationButton(
                                onClick = openAiChat,
                                label = destination.title,
                                modifier = Modifier.weight(1f),
                            )
                        } else {
                            BottomNavigationButton(
                                onClick = {
                                    backStack.setRoot(Route.NestedNav.Home)
                                    if (destination.route != Route.NestedNav.Home) {
                                        backStack.push(destination.route)
                                    }
                                },
                                icon = if (selected) destination.selectedIcon else destination.icon,
                                label = destination.title,
                                selected = selected,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }
        },
    ) { innerPadding ->
        NavDisplay(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            backStack = backStack,
            onBack = {
                if (backStack.lastOrNull() == Route.NestedNav.Home) {
                    navigateBack()
                } else {
                    backStack.pop()
                }
            },
            transitionSpec = {
                fadeIn(tween(NAVIGATION_DURATION_MILLIS)) togetherWith
                        fadeOut(tween(NAVIGATION_DURATION_MILLIS))
            },
            popTransitionSpec = {
                (slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(NAVIGATION_DURATION_MILLIS),
                    initialOffset = { it / 3 },
                ) + fadeIn(tween(NAVIGATION_DURATION_MILLIS))) togetherWith
                        (slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.End,
                            animationSpec = tween(NAVIGATION_DURATION_MILLIS),
                            targetOffset = { it / 3 },
                        ) + fadeOut(tween(NAVIGATION_DURATION_MILLIS)))
            },
            predictivePopTransitionSpec = { _ ->
                (slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(NAVIGATION_DURATION_MILLIS),
                    initialOffset = { it / 3 },
                ) + fadeIn(tween(NAVIGATION_DURATION_MILLIS))) togetherWith
                        (slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.End,
                            animationSpec = tween(NAVIGATION_DURATION_MILLIS),
                            targetOffset = { it / 3 },
                        ) + fadeOut(tween(NAVIGATION_DURATION_MILLIS)))
            },
            entryProvider = entryProvider {
                entry<Route.NestedNav.Home> {
                    HomeRoot(
                        onOpenNotifications = openNotifications,
                        onViewAllOrders = {
                            backStack.setRoot(Route.NestedNav.Home)
                            backStack.push(Route.NestedNav.Orders)
                        },
                        onOrderClick = { orderId ->
                            openOrderDetails(orderId)
                        }
                    )
                }
                entry<Route.NestedNav.Requests> {
                    RequestsRoot(
                        onRequestClick = openRequestDetails,
                    )
                }
                entry<Route.NestedNav.Orders> {
                    OrdersRoot(
                        onOrderClick = openOrderDetails,
                    )
                }
                entry<Route.NestedNav.Profile> {
                    ProfileRoot(
                        openLogin = openLogin,
                        openInvitePharmacist = openInvitePharmacist,
                        openPharmacistsList = openPharmacistsList,
                        openPersonalInfo = openPersonalInfo
                    )
                }
            },
        )
    }
}
