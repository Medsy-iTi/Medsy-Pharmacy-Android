package com.medsy.pharmacy.nav.nested

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.ShortNavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight

@Composable
fun BottomNavigationButton(
    onClick: () -> Unit,
    @DrawableRes icon: Int,
    @StringRes label: Int,
    selected: Boolean,
    modifier: Modifier = Modifier,
) {
    ShortNavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            Icon(
                painter = painterResource(icon),
                contentDescription = stringResource(label),
            )
        },
        label = {
            Text(
                text = stringResource(label),
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            )
        },
        modifier = modifier,
        colors = ShortNavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            selectedIndicatorColor = Color.Transparent,
        ),
    )
}
