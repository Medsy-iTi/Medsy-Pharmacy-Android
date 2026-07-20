package com.medsy.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.medsy.designsystem.components.MedsyShimmer
import com.medsy.designsystem.R as DesignR
import com.medsy.presentation.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SplashRoot(
    openOnBoarding: () -> Unit,
    openLogin: () -> Unit,
    openHome: () -> Unit,
    openNoPharmacy: () -> Unit,
    openPendingApproval: () -> Unit,
    openRejected: () -> Unit,
    openSuspended: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                SplashUIEffect.OpenHome -> openHome()
                SplashUIEffect.OpenLogin -> openLogin()
                SplashUIEffect.OpenNoPharmacy -> openNoPharmacy()
                SplashUIEffect.OpenOnboarding -> openOnBoarding()
                SplashUIEffect.OpenPendingApproval -> openPendingApproval()
                SplashUIEffect.OpenRejected -> openRejected()
                SplashUIEffect.OpenSuspended -> openSuspended()
            }
        }
    }
    SplashScreen()
}

@Composable
fun SplashScreen() {
    val isDark = isSystemInDarkTheme()

    val logoScale = remember { Animatable(0.55f) }
    val logoAlpha = remember { Animatable(0f) }
    val taglineAlpha = remember { Animatable(0f) }
    val taglineTranslationY = remember { Animatable(SplashConstants.TEXT_SLIDE_START_OFFSET) }

    LaunchedEffect(Unit) {
        launch {
            logoScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium,
                ),
            )
        }
        launch {
            logoAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = SplashConstants.LOGO_ANIMATION_DURATION,
                    easing = FastOutSlowInEasing
                ),
            )
        }

        // Tagline & App Name text slide + fade with configured delay
        delay(SplashConstants.LOGO_ANIMATION_DELAY.milliseconds)

        launch {
            taglineAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = SplashConstants.TEXT_ANIMATION_DURATION,
                    easing = FastOutSlowInEasing
                ),
            )
        }
        launch {
            taglineTranslationY.animateTo(
                targetValue = SplashConstants.TEXT_SLIDE_END_OFFSET,
                animationSpec = tween(
                    durationMillis = SplashConstants.TEXT_ANIMATION_DURATION,
                    easing = FastOutSlowInEasing
                ),
            )
        }

        // Hold duration after animation finishes
        delay(SplashConstants.HOLD_DURATION.milliseconds)
    }

    val backgroundBrush = if (isDark) {
        Brush.radialGradient(
            colors = listOf(
                MaterialTheme.colorScheme.primaryContainer,
                MaterialTheme.colorScheme.background,
            ),
            center = Offset(0.5f, 0.35f),
            radius = 1200f,
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                MaterialTheme.colorScheme.surfaceVariant,
                MaterialTheme.colorScheme.surface,
            ),
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            MedsyShimmer(
                modifier = Modifier
                    .size(180.dp)
                    .scale(logoScale.value)
                    .alpha(logoAlpha.value),
            ) {
                Image(
                    painter = painterResource(DesignR.drawable.ic_logo_transparent),
                    contentDescription = stringResource(R.string.splash_logo_description),
                    modifier = Modifier.fillMaxSize(),
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .graphicsLayer(translationY = taglineTranslationY.value)
                    .alpha(taglineAlpha.value)
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp,
                        letterSpacing = (-0.5).sp,
                    ),
                    color = if (isDark) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stringResource(R.string.splash_tagline),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        letterSpacing = 0.3.sp,
                    ),
                    color = if (isDark) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                )
            }
        }
    }
}
