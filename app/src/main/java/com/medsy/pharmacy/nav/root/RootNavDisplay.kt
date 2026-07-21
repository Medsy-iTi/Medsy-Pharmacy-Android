package com.medsy.pharmacy.nav.root

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.medsy.pharmacy.nav.NAVIGATION_DURATION_MILLIS
import com.medsy.pharmacy.nav.nested.NestedNavDisplay
import com.medsy.presentation.auth.approval.ApprovalRoot
import com.medsy.presentation.auth.approval.ApprovalScreenStatus
import com.medsy.presentation.auth.login.LoginRoot
import com.medsy.presentation.auth.nopharmacy.NoPharmacyRoot
import com.medsy.presentation.auth.otp.OtpRoot
import com.medsy.presentation.auth.register.RegistrationRoot
import com.medsy.presentation.auth.registerpharmacy.PharmacyRegistrationRoot
import com.medsy.presentation.onboarding.OnboardingRoot
import com.medsy.presentation.orderdetails.OrderDetailsRoot
import com.medsy.presentation.splash.SplashRoot

@Composable
fun RootNavDisplay() {
    val backStack = rememberNavBackStack(Route.Splash)

    fun replaceWith(route: Route) {
        backStack.clear()
        backStack.navigateSingleTop(route)
    }

    NavDisplay(
        modifier = Modifier.fillMaxSize(),
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        transitionSpec = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(NAVIGATION_DURATION_MILLIS),
            ) togetherWith slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(NAVIGATION_DURATION_MILLIS),
            )
        },
        popTransitionSpec = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(NAVIGATION_DURATION_MILLIS),
            ) togetherWith slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(NAVIGATION_DURATION_MILLIS),
            )
        },
        entryProvider = entryProvider {
            entry<Route.Splash> {
                SplashRoot(
                    openOnBoarding = { replaceWith(Route.Onboarding) },
                    openLogin = { replaceWith(Route.Login) },
                    openHome = { replaceWith(Route.NestedNav) },
                    openNoPharmacy = { replaceWith(Route.NoPharmacy) },
                    openPendingApproval = { replaceWith(Route.PendingApproval) },
                    openRejected = { replaceWith(Route.Rejected) },
                    openSuspended = { replaceWith(Route.Suspended) },
                )
            }
            entry<Route.Onboarding> {
                OnboardingRoot(openLogin = { replaceWith(Route.Login) })
            }
            entry<Route.Login> {
                LoginRoot(
                    openHome = { replaceWith(Route.NestedNav) },
                    openNoPharmacy = { replaceWith(Route.NoPharmacy) },
                    openRegistration = { backStack.navigateSingleTop(Route.Registration) },
                )
            }
            entry<Route.NoPharmacy> {
                NoPharmacyRoot(
                    openPharmacyRegistration = {
                        backStack.navigateSingleTop(Route.PharmacyRegistration)
                    },
                    openLogin = { replaceWith(Route.Login) },
                )
            }
            entry<Route.Registration> {
                RegistrationRoot(
                    onNavigateBack = { backStack.removeLastOrNull() },
                    onNavigateToSignIn = { replaceWith(Route.Login) },
                    onNavigateToOtp = { email ->
                        backStack.navigateSingleTop(Route.OTPVerification(email))
                    },
                )
            }
            entry<Route.OTPVerification> { route ->
                OtpRoot(
                    email = route.email,
                    onNavigateNoPharmacy = { replaceWith(Route.NoPharmacy) },
                    onNavigateBack = { backStack.removeLastOrNull() },
                )
            }
            entry<Route.PharmacyRegistration> {
                PharmacyRegistrationRoot(
                    openPendingApproval = { replaceWith(Route.PendingApproval) },
                )
            }
            entry<Route.PendingApproval> {
                ApprovalRoot(
                    status = ApprovalScreenStatus.Pending,
                    openLogin = { replaceWith(Route.Login) },
                )
            }
            entry<Route.Rejected> {
                ApprovalRoot(
                    status = ApprovalScreenStatus.Rejected,
                    openLogin = { replaceWith(Route.Login) },
                )
            }
            entry<Route.Suspended> {
                ApprovalRoot(
                    status = ApprovalScreenStatus.Suspended,
                    openLogin = { replaceWith(Route.Login) },
                )
            }
            entry<Route.NestedNav> {
                NestedNavDisplay(
                    navigateBack = { backStack.removeLastOrNull() },
                    openLogin = { replaceWith(Route.Login) },
                    openOrderDetails = { orderId ->
                        backStack.navigateSingleTop(Route.OrderDetails(orderId))
                    },
                    openInvitePharmacist = { backStack.navigateSingleTop(Route.InvitePharmacist) },
                    openPharmacistsList = { backStack.navigateSingleTop(Route.PharmacistsList) },
                    openPersonalInfo = { backStack.navigateSingleTop(Route.PersonalInfo) }
                )
            }
            entry<Route.OrderDetails> { route ->
                val context = LocalContext.current
                OrderDetailsRoot(
                    orderId = route.orderId,
                    onNavigateBack = { backStack.removeLastOrNull() },
                    onDialPhoneNumber = { phoneNumber ->
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
                        context.startActivity(intent)
                    },
                    onOpenLocationOnMap = { },
                    onOpenPaymentSummary = { },
                    onOpenCustomerChat = { },
                )
            }
            entry<Route.InvitePharmacist> {
                com.medsy.presentation.profile.invite.InvitePharmacistRoot(
                    navigateBack = { backStack.removeLastOrNull() },
                    navigateToInvitationSent = { backStack.navigateSingleTop(Route.InvitationSent) }
                )
            }
            entry<Route.InvitationSent> {
                com.medsy.presentation.profile.invitationsent.InvitationSentRoot(
                    navigateBack = { backStack.removeLastOrNull() },
                    returnToProfile = {
                        while (backStack.isNotEmpty() && backStack.last() != Route.NestedNav) {
                            backStack.removeLastOrNull()
                        }
                    }
                )
            }
            entry<Route.PharmacistsList> {
                com.medsy.presentation.profile.pharmacists.PharmacistsListRoot(
                    navigateBack = { backStack.removeLastOrNull() },
                    navigateToInvitePharmacist = { backStack.navigateSingleTop(Route.InvitePharmacist) }
                )
            }
            entry<Route.PersonalInfo> {
                com.medsy.presentation.profile.personalinfo.PersonalInfoRoot(
                    navigateBack = { backStack.removeLastOrNull() }
                )
            }
        },
    )
}
