package com.medsy.designsystem.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ─── Dark colour scheme (Optimized: lighter slate-gray/navy) ─────────────────
private val DarkColorScheme = darkColorScheme(
    primary = MedsyBlue,
    onPrimary = NeutralWhite,
    primaryContainer = Color(0xFF1E2E4A),      // Deep slate-blue container
    onPrimaryContainer = Color(0xFFBDD8FF),
    secondary = MedsyBlueDark,
    onSecondary = NeutralWhite,
    secondaryContainer = Color(0xFF1B233A),    // Slate secondary container
    onSecondaryContainer = Color(0xFF93C5FD),
    tertiary = MedsyBlueLight,
    onTertiary = NeutralWhite,
    background = Color(0xFF181B26),            // Lighter dark background (slate blue-gray)
    onBackground = NeutralWhite,
    surface = Color(0xFF222736),               // Lighter slate surface for cards and bottom nav
    onSurface = NeutralWhite,
    surfaceVariant = Color(0xFF2D3349),        // Surface variant for text fields/chips
    onSurfaceVariant = SecondaryText,
    error = ErrorRed,
    errorContainer = Color(0xFF5A1A1A),
    onErrorContainer = Color(0xFFFCA5A5),
    outline = Color(0xFF3B4461),               // Premium outline for dividers
)

// ─── Light colour scheme ──────────────────────────────────────────────────────
private val LightColorScheme = lightColorScheme(
    primary = MedsyBlue,
    onPrimary = NeutralWhite,
    primaryContainer = MedsyBlueSurface,
    onPrimaryContainer = MedsyBlueDark,
    secondary = MedsyBlueDark,
    onSecondary = NeutralWhite,
    secondaryContainer = Color(0xFFE0E9FF),
    onSecondaryContainer = Color(0xFF0D1E5E),
    tertiary = MedsyBlueLight,
    onTertiary = NeutralWhite,
    background = OffWhiteBg,
    onBackground = PrimaryText,
    surface = NeutralWhite,
    onSurface = PrimaryText,
    surfaceVariant = MedsyBlueSurface,
    onSurfaceVariant = SecondaryText,
    error = ErrorRed,
    errorContainer = Color(0xFFFEF2F2),
    onErrorContainer = ErrorRed,
    outline = MedsyBlueBorder,
)

@Composable
fun MedsyTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    val extendedColors = if (darkTheme) darkExtendedColors else lightExtendedColors

    CompositionLocalProvider(
        LocalExtendedColors provides extendedColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
