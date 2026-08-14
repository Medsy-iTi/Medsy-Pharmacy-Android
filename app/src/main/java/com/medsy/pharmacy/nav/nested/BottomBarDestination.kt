package com.medsy.pharmacy.nav.nested

import com.medsy.pharmacy.nav.root.Route
import com.medsy.presentation.R as PresentationR

enum class BottomBarDestination(
    val title: Int,
    val icon: Int,
    val selectedIcon: Int,
    val route: Route,
    val isProminent: Boolean = false,
) {
    Home(
        title = PresentationR.string.home_nav_home,
        icon = PresentationR.drawable.home,
        selectedIcon = PresentationR.drawable.home,
        route = Route.NestedNav.Home,
    ),
    Requests(
        title = PresentationR.string.home_nav_requests,
        icon = PresentationR.drawable.orderlist,
        selectedIcon = PresentationR.drawable.orderlist,
        route = Route.NestedNav.Requests,
    ),
    AiChat(
        title = PresentationR.string.ai_chat_title,
        icon = PresentationR.drawable.home,
        selectedIcon = PresentationR.drawable.home,
        route = Route.AiChat,
        isProminent = true,
    ),
    Orders(
        title = PresentationR.string.home_nav_orders,
        icon = PresentationR.drawable.order_history,
        selectedIcon = PresentationR.drawable.order_history,
        route = Route.NestedNav.Orders,
    ),
    Profile(
        title = PresentationR.string.profile_title,
        icon = PresentationR.drawable.person_outlined,
        selectedIcon = PresentationR.drawable.person,
        route = Route.NestedNav.Profile,
    ),
}
