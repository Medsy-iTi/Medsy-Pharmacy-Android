package com.medsy.presentation.onboarding

import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.medsy.designsystem.ui.theme.MedsyTheme
import com.medsy.presentation.R
import com.medsy.presentation.common.PlaceholderScaffold

@Composable
fun OnboardingRoot(
    openLogin: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    LaunchedEffect(viewModel) {
        viewModel.effect.collect {
            when (it) {
                OnboardingUIEffect.OpenLogin -> openLogin()
            }
        }
    }
    OnboardingScreen(onIntent = viewModel::onIntent)
}

@Composable
fun OnboardingScreen(
    onIntent: (OnboardingUIIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    PlaceholderScaffold(
        title = stringResource(R.string.onboarding_title),
        supportingText = stringResource(R.string.onboarding_supporting),
        modifier = modifier,
    ) {
        listOf(
            R.string.onboarding_receive_requests,
            R.string.onboarding_send_offers,
            R.string.onboarding_manage_orders,
        ).forEach { text ->
            Card {
                Text(text = stringResource(text), modifier = Modifier)
            }
        }
        Button(onClick = { onIntent(OnboardingUIIntent.GetStarted) }) {
            Text(stringResource(R.string.action_get_started))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingPreview() {
    MedsyTheme {
        OnboardingScreen(onIntent = {})
    }
}
