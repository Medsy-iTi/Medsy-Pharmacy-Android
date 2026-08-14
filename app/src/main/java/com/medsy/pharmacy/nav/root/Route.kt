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
    data object PharmacyRegistration : Route

    @Serializable
    data class OTPVerification(val email: String) : Route

    @Serializable
    data object NoPharmacy : Route

    @Serializable
    data object NoPharmacyInvitations : Route

    @Serializable
    data object PendingApproval : Route

    @Serializable
    data object Rejected : Route

    @Serializable
    data object Suspended : Route

    @Serializable
    data class SubstituteSearch(val requestItemId: Long) : Route

    @Serializable
    data object ActiveOrders : Route

    @Serializable
    data object Notifications : Route

    @Serializable
    data object AiChat : Route

    @Serializable
    data class RequestDetails(val requestId: Long) : Route

    @Serializable
    data class OrderDetails(val orderId: Long) : Route

    @Serializable
    data object InvitePharmacist : Route

    @Serializable
    data object InvitationSent : Route

    @Serializable
    data object PharmacistsList : Route

    @Serializable
    data object PersonalInfo : Route

    @Serializable
    data object NestedNav : Route {
        @Serializable
        data object Home : Route

        @Serializable
        data object Requests : Route

        @Serializable
        data object Orders : Route

        @Serializable
        data object Profile : Route
    }
}
