package com.medsy.presentation.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.medsy.designsystem.R as DesignR
import com.medsy.presentation.R

@Composable
fun SplashRoot(
    openOnBoarding: () -> Unit,
    openLogin: () -> Unit,
    openHome: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    LaunchedEffect(viewModel) {
        viewModel.effect.collect {effect ->
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(DesignR.drawable.ic_logo_transparent),
            contentDescription = stringResource(R.string.splash_logo_description),
            modifier = Modifier.size(180.dp),
        )
    }
}
