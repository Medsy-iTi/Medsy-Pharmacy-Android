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
import androidx.compose.ui.graphics.Color
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

@Composable
fun SplashRoot(
    openOnBoarding: () -> Unit,
    openLogin: () -> Unit,
    openHome: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                SplashUIEffect.OpenHome -> openHome()
                SplashUIEffect.OpenLogin -> openLogin()
                SplashUIEffect.OpenOnboarding -> openOnBoarding()
                SplashUIEffect.OpenPendingApproval -> TODO()
                SplashUIEffect.OpenRejected -> TODO()
                SplashUIEffect.OpenSuspended -> TODO()
            }
        }
    }
    SplashScreen()
}

@Composable
fun SplashScreen() {
    val isDark = isSystemInDarkTheme()

    // ── Animation state ────────────────────────────────────────────────────────
    val logoScale = remember { Animatable(0.55f) }
    val logoAlpha = remember { Animatable(0f) }
    val taglineAlpha = remember { Animatable(0f) }
    val taglineTranslationY = remember { Animatable(20f) }

    LaunchedEffect(Unit) {
        // Logo: spring scale-in + simultaneous fade-in
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
                animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing),
            )
        }
        // Tagline: slide-up + fade-in after a short delay
        delay(380)
        launch {
            taglineAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing),
            )
        }
        launch {
            taglineTranslationY.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
            )
        }
    }

    // ── Background gradient ────────────────────────────────────────────────────
    val backgroundBrush = if (isDark) {
        Brush.radialGradient(
            colors = listOf(
                Color(0xFF0D2A5E),  // Deep navy centre
                Color(0xFF060D1A),  // Near-black edge
            ),
            center = Offset(0.5f, 0.35f),
            radius = 1200f,
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFEEF4FF),  // Light blue top
                Color(0xFFFFFFFF),  // White bottom
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
            // ── Shimmer logo ───────────────────────────────────────────────────
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

            // ── App name ───────────────────────────────────────────────────────
            Text(
                text = "Medsy",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    letterSpacing = (-0.5).sp,
                ),
                color = if (isDark) Color(0xFF93C5FD) else MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .alpha(logoAlpha.value)
                    .scale(logoScale.value),
            )

            Spacer(modifier = Modifier.height(6.dp))

            // ── Tagline ────────────────────────────────────────────────────────
            Text(
                text = stringResource(R.string.splash_tagline),
                style = MaterialTheme.typography.bodyMedium.copy(
                    letterSpacing = 0.3.sp,
                ),
                color = if (isDark) Color(0xFF60A5FA) else MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                modifier = Modifier
                    .alpha(taglineAlpha.value),
            )
        }
    }
}
