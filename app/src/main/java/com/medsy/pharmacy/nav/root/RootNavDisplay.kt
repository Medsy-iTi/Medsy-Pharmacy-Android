package com.medsy.pharmacy.nav.root

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.medsy.pharmacy.nav.nested.NestedNavDisplay
import com.medsy.pharmacy.nav.openDialer
import com.medsy.pharmacy.nav.openImage
import com.medsy.pharmacy.nav.openLocation
import com.medsy.presentation.auth.approval.ApprovalRoot
import com.medsy.presentation.auth.approval.ApprovalScreenStatus
import com.medsy.presentation.auth.login.LoginRoot
import com.medsy.presentation.auth.nopharmacy.NoPharmacyRoot
import com.medsy.presentation.auth.nopharmacy.invitation.NoPharmacyInvitationsRoot
import com.medsy.presentation.auth.otp.OtpRoot
import com.medsy.presentation.auth.register.RegistrationRoot
import com.medsy.presentation.auth.registerpharmacy.PharmacyRegistrationRoot
import com.medsy.presentation.notifications.NotificationsRoot
import com.medsy.presentation.onboarding.OnboardingRoot
import com.medsy.presentation.orderdetails.RequestDetailsRoot
import com.medsy.presentation.orderdetails.SubstituteSearchRoot
import com.medsy.presentation.orderdetails.offer.OfferDetailsRoot
import com.medsy.presentation.orders.orderdetails.OrderDetailsRoot
import com.medsy.presentation.profile.invitationsent.InvitationSentRoot
import com.medsy.presentation.profile.invite.InvitePharmacistRoot
import com.medsy.presentation.profile.personalinfo.PersonalInfoRoot
import com.medsy.presentation.profile.pharmacists.PharmacistsListRoot
import com.medsy.presentation.splash.SplashRoot

const val NAVIGATION_DURATION_MILLIS = 350

@Composable
fun RootNavDisplay(
    pendingRequestId: Long? = null,
    onPendingRequestConsumed: () -> Unit = {}
) {
    val backStack = rememberNavBackStack(Route.Splash)

    LaunchedEffect(pendingRequestId, backStack.lastOrNull()) {
        if (pendingRequestId != null && backStack.firstOrNull() == Route.NestedNav) {
            backStack.push(Route.RequestDetails(pendingRequestId))
            onPendingRequestConsumed()
        }
    }

    NavDisplay(
        modifier = Modifier.fillMaxSize(),
        backStack = backStack,
        onBack = { backStack.pop() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        transitionSpec = {
            fadeIn(tween(NAVIGATION_DURATION_MILLIS)) togetherWith
                    fadeOut(tween(NAVIGATION_DURATION_MILLIS))
        },
        popTransitionSpec = {
            fadeIn(tween(NAVIGATION_DURATION_MILLIS)) togetherWith
                    fadeOut(tween(NAVIGATION_DURATION_MILLIS))
        },
        predictivePopTransitionSpec = { _ ->
            fadeIn(tween(NAVIGATION_DURATION_MILLIS)) togetherWith
                    fadeOut(tween(NAVIGATION_DURATION_MILLIS))
        },
        entryProvider = entryProvider {
            entry<Route.Splash> {
                SplashRoot(
                    openOnBoarding = { backStack.setRoot(Route.Onboarding) },
                    openLogin = { backStack.setRoot(Route.Login) },
                    openHome = { backStack.setRoot(Route.NestedNav) },
                    openNoPharmacy = { backStack.setRoot(Route.NoPharmacy) },
                    openPendingApproval = { backStack.setRoot(Route.PendingApproval) },
                    openRejected = { backStack.setRoot(Route.Rejected) },
                    openSuspended = { backStack.setRoot(Route.Suspended) },
                )
            }
            entry<Route.Onboarding> {
                OnboardingRoot(openLogin = { backStack.setRoot(Route.Login) })
            }
            entry<Route.Login> {
                LoginRoot(
                    openHome = { backStack.setRoot(Route.NestedNav) },
                    openNoPharmacy = { backStack.setRoot(Route.NoPharmacy) },
                    openRegistration = { backStack.push(Route.Registration) },
                )
            }
            entry<Route.NoPharmacy> {
                NoPharmacyRoot(
                    openPharmacyRegistration = {
                        backStack.push(Route.PharmacyRegistration)
                    },
                    openLogin = { backStack.setRoot(Route.Login) },
                    openInvitations = { backStack.push(Route.NoPharmacyInvitations) },
                )
            }
            entry<Route.NoPharmacyInvitations> {
                NoPharmacyInvitationsRoot(
                    navigateBack = { backStack.pop() },
                    navigateHome = { backStack.setRoot(Route.NestedNav) },
                )
            }
            entry<Route.Registration> {
                RegistrationRoot(
                    onNavigateBack = { backStack.pop() },
                    onNavigateToSignIn = { backStack.setRoot(Route.Login) },
                    onNavigateToOtp = { email ->
                        backStack.push(Route.OTPVerification(email))
                    },
                )
            }
            entry<Route.OTPVerification> { route ->
                OtpRoot(
                    email = route.email,
                    onNavigateNoPharmacy = { backStack.setRoot(Route.NoPharmacy) },
                    onNavigateBack = { backStack.pop() },
                )
            }
            entry<Route.PharmacyRegistration> {
                PharmacyRegistrationRoot(
                    openPendingApproval = { backStack.setRoot(Route.PendingApproval) },
                )
            }
            entry<Route.PendingApproval> {
                ApprovalRoot(
                    status = ApprovalScreenStatus.Pending,
                    openLogin = { backStack.setRoot(Route.Login) },
                )
            }
            entry<Route.Rejected> {
                ApprovalRoot(
                    status = ApprovalScreenStatus.Rejected,
                    openLogin = { backStack.setRoot(Route.Login) },
                )
            }
            entry<Route.Suspended> {
                ApprovalRoot(
                    status = ApprovalScreenStatus.Suspended,
                    openLogin = { backStack.setRoot(Route.Login) },
                )
            }
            entry<Route.NestedNav> {
                NestedNavDisplay(
                    navigateBack = { backStack.pop() },
                    openLogin = { backStack.setRoot(Route.Login) },
                    openRequestDetails = { requestId ->
                        backStack.push(Route.RequestDetails(requestId))
                    },
                    openOfferDetails = { offerId -> backStack.push(Route.OfferDetails(offerId)) },
                    openOrderDetails = { orderId -> backStack.push(Route.OrderDetails(orderId)) },
                    openInvitePharmacist = { backStack.push(Route.InvitePharmacist) },
                    openPharmacistsList = { backStack.push(Route.PharmacistsList) },
                    openPersonalInfo = { backStack.push(Route.PersonalInfo) },
                    openNotifications = { backStack.push(Route.Notifications) }
                )
            }
            entry<Route.Notifications> {
                NotificationsRoot(
                    onNavigateBack = { backStack.pop() },
                    onNotificationClick = { requestId ->
                        backStack.push(Route.RequestDetails(requestId))
                    }
                )
            }
            entry<Route.RequestDetails> { route ->
                val context = LocalContext.current
                RequestDetailsRoot(
                    requestId = route.requestId,
                    onNavigateBack = { backStack.pop() },
                    onDialPhoneNumber = context::openDialer,
                    onOpenLocationOnMap = context::openLocation,
                    onOpenPaymentSummary = { },
                    onNavigateToSubstituteSearch = { itemId ->
                        backStack.push(Route.SubstituteSearch(itemId))
                    },
                    onOpenPrescriptionImage = context::openImage
                )
            }
            entry<Route.OfferDetails> { route ->
                OfferDetailsRoot(
                    offerId = route.offerId,
                    onNavigateBack = { backStack.pop() },
                )
            }
            entry<Route.OrderDetails> { route ->
                val context = LocalContext.current
                OrderDetailsRoot(
                    orderId = route.orderId,
                    onNavigateBack = { backStack.pop() },
                    onDialPhoneNumber = context::openDialer,
                    onOpenLocationOnMap = context::openLocation,
                )
            }
            entry<Route.SubstituteSearch> { route ->
                SubstituteSearchRoot(
                    requestItemId = route.requestItemId,
                    onNavigateBack = { backStack.pop() }
                )
            }
            entry<Route.InvitePharmacist> {
                InvitePharmacistRoot(
                    navigateBack = { backStack.pop() },
                    navigateToInvitationSent = { backStack.push(Route.InvitationSent) }
                )
            }
            entry<Route.InvitationSent> {
                InvitationSentRoot(
                    navigateBack = { backStack.pop() },
                    returnToProfile = { backStack.popTo(Route.NestedNav) }
                )
            }
            entry<Route.PharmacistsList> {
                PharmacistsListRoot(
                    navigateBack = { backStack.pop() },
                    navigateToInvitePharmacist = { backStack.push(Route.InvitePharmacist) }
                )
            }
            entry<Route.PersonalInfo> {
                PersonalInfoRoot(
                    navigateBack = { backStack.pop() }
                )
            }
        },
    )
}
