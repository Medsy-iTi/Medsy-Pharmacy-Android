package com.medsy.designsystem.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class ExtendedColors(
    val success: Color,
    val onSuccess: Color,
    val blueContainer: Color,
    val blueContent: Color,
    val orangeContainer: Color,
    val orangeContent: Color,
    val prescriptionSuccessSoft: Color,
    val prescriptionSuccessContent: Color,
    val purpleContainer: Color,
    val purpleContent: Color,
    val neutralContainer: Color,
    val neutralContent: Color,
    val darkBlueColor: Color,
    val redContainer: Color,
    val redContent: Color,
    val greenContainer: Color,
    val greenContent: Color
)

internal val LocalExtendedColors = staticCompositionLocalOf {
    lightExtendedColors
}

val MaterialTheme.extendedColors: ExtendedColors
    @Composable
    get() = LocalExtendedColors.current

internal val lightExtendedColors = ExtendedColors(
    success = Color(0xFF166534),
    onSuccess = Color(0xFFFFFFFF),
    blueContainer = Color(0xFFDCEBFF),
    blueContent = MedsyBlue,
    orangeContainer = Color(0xFFFFEDD5),
    orangeContent = Color(0xFFF97316),
    prescriptionSuccessSoft = Color(0xFFE5F4ED),
    prescriptionSuccessContent = Color(0xFF16A34A),
    purpleContainer = Color(0xFFF3E8FF),
    purpleContent = Color(0xFF8B5CF6),
    neutralContainer = Color(0xFFF3F4F6),
    neutralContent = Color(0xFF6B7280),
    darkBlueColor = Color(0xFF010326),
    redContainer = Color(0xFFFEE2E2),
    redContent = Color(0xFFEF4444),
    greenContainer = Color(0xFFDCFCE7),
    greenContent = Color(0xFF22C55E),
)

internal val darkExtendedColors = ExtendedColors(
    success = Color(0xFF78E29A),
    onSuccess = Color(0xFF00391C),
    blueContainer = Color(0xFF1E2E4A),
    blueContent = Color(0xFF93C5FD),
    orangeContainer = Color(0xFF431407),
    orangeContent = Color(0xFFFB923C),
    prescriptionSuccessSoft = Color(0xFF0D3321),
    prescriptionSuccessContent = Color(0xFF86EFAC),
    purpleContainer = Color(0xFF2E1065),
    purpleContent = Color(0xFFD8B4FE),
    neutralContainer = Color(0xFF1F2937),
    neutralContent = Color(0xFF9CA3AF),
    darkBlueColor = Color(0xFFFFFFFF),
    redContainer = Color(0xFF450A0A),
    redContent = Color(0xFFF87171),
    greenContainer = Color(0xFF064E3B),
    greenContent = Color(0xFF4ADE80),
)
