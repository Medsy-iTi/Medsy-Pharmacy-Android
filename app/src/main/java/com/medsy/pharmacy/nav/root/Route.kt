package com.medsy.pharmacy.nav.root

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Route : NavKey {
    @Serializable
    data object Splash : Route

    @Serializable
    data object Onboarding : Route

    @Serializable
    data object Login : Route

    @Serializable
    data object Registration : Route

    @Serializable
    data object Verification : Route

    @Serializable
    data object NoPharmacy : Route

    @Serializable
    data object PendingApproval : Route

    @Serializable
    data object Rejected : Route

    @Serializable
    data object Suspended : Route

    @Serializable
    data object NestedNav : Route {
        @Serializable
        data object Home : Route

        @Serializable
        data object Orders : Route

        @Serializable
        data object Profile : Route
    }
}
