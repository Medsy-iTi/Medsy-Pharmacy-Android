package com.medsy.presentation.orders

import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.medsy.designsystem.ui.theme.MedsyTheme
import com.medsy.presentation.R
import com.medsy.presentation.common.PlaceholderScaffold

@Composable
fun OrdersRoot() {
    OrdersScreen()
}

@Composable
fun OrdersScreen(
    modifier: Modifier = Modifier,
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    PlaceholderScaffold(
        title = stringResource(R.string.orders_title),
        supportingText = stringResource(R.string.orders_supporting),
        modifier = modifier,
    ) {
        PrimaryTabRow(selectedTabIndex = selectedTab) {
            listOf(R.string.orders_active, R.string.orders_history).forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(stringResource(title)) },
                )
            }
        }
        Text(
            stringResource(
                if (selectedTab == 0) {
                    R.string.orders_active_empty
                } else {
                    R.string.orders_history_empty
                },
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OrdersPreview() {
    MedsyTheme {
        OrdersScreen()
    }
}
