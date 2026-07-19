package com.medsy.pharmacy.nav.nested

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.medsy.pharmacy.nav.NAVIGATION_DURATION_MILLIS
import com.medsy.pharmacy.nav.root.Route
import com.medsy.pharmacy.nav.root.navigateSingleTop
import com.medsy.presentation.home.HomeRoot
import com.medsy.presentation.orders.OrdersRoot
import com.medsy.presentation.profile.ProfileRoot
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
fun NestedNavDisplay(
    navigateBack: () -> Unit,
    openLogin: () -> Unit,
    openOrderDetails: (String) -> Unit,

    ) {
    val backStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Route.NestedNav.Home::class, Route.NestedNav.Home.serializer())
                    subclass(Route.NestedNav.Orders::class, Route.NestedNav.Orders.serializer())
                    subclass(Route.NestedNav.Profile::class, Route.NestedNav.Profile.serializer())
                }
            }
        },
        Route.NestedNav.Home,
    )

    val isDark = isSystemInDarkTheme()

    Scaffold(
        bottomBar = {
            Column {
                HorizontalDivider(
                    color = if (isDark) Color.White.copy(alpha = 0.12f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.16f),
                    thickness = 1.dp
                )
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.background,
                    tonalElevation = 0.dp
                ) {
                    BottomBarDestination.entries.forEach { destination ->
                        val selected = backStack.lastOrNull() == destination.route
                        BottomNavigationButton(
                            onClick = {
                                backStack.apply {
                                    clear()
                                    if (destination.route != Route.NestedNav.Home) {
                                        navigateSingleTop(Route.NestedNav.Home)
                                    }
                                    navigateSingleTop(destination.route)
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
                    backStack.removeLastOrNull()
                }
            },
            transitionSpec = {
                fadeIn(tween(NAVIGATION_DURATION_MILLIS)) togetherWith
                    fadeOut(tween(NAVIGATION_DURATION_MILLIS))
            },
            entryProvider = entryProvider {
                entry<Route.NestedNav.Home> { HomeRoot() }
                entry<Route.NestedNav.Orders> { OrdersRoot(onOrderClick = openOrderDetails) }
                entry<Route.NestedNav.Profile> { ProfileRoot(openLogin = openLogin) }
            },
        )
    }
}
