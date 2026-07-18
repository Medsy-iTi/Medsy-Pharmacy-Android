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
    // Category Colors
    val blueContainer: Color,
    val blueContent: Color,
    val orangeContainer: Color,
    val orangeContent: Color,
    val pinkContainer: Color,
    val pinkContent: Color,
    val purpleContainer: Color,
    val purpleContent: Color,
    val neutralContainer: Color,
    val neutralContent: Color,

    val categoryMoreBg: Color,
    val categoryMoreIcon: Color,

    val prescriptionPrimary: Color,
    val prescriptionTitle: Color,
    val prescriptionSupporting: Color,
    val prescriptionSuccessContainer: Color,
    val prescriptionSuccessSoft: Color,
    val prescriptionSuccessContent: Color,
    val prescriptionWarningContainer: Color,
    val prescriptionWarningContent: Color,
    val prescriptionWarningBorder: Color,
    val prescriptionErrorContainer: Color,
    val prescriptionErrorContent: Color,
    val prescriptionDisabledContainer: Color,
    val prescriptionGalleryContainer: Color,
    val prescriptionGalleryContent: Color,
    val prescriptionScanBorder: Color,
)

internal val LocalExtendedColors = staticCompositionLocalOf {
    lightExtendedColors
}

val MaterialTheme.extendedColors: ExtendedColors
    @Composable
    get() = LocalExtendedColors.current

// ─── Light extended colours ───────────────────────────────────────────────────
internal val lightExtendedColors = ExtendedColors(
    success = SuccessGreen,
    onSuccess = Color.White,
    // Category chips — keep distinct from primary blue
    blueContainer = Color(0xFFDCEBFF),
    blueContent = MedsyBlue,
    orangeContainer = Color(0xFFFFEDD5),
    orangeContent = Color(0xFFF97316),
    pinkContainer = Color(0xFFFCE7F3),
    pinkContent = Color(0xFFEC4899),
    purpleContainer = Color(0xFFF3E8FF),
    purpleContent = Color(0xFF8B5CF6),
    neutralContainer = Color(0xFFF3F4F6),
    neutralContent = Color(0xFF6B7280),

    categoryMoreBg = Color(0xFFEEF4FF),
    categoryMoreIcon = MedsyBlue,

    // Prescription — shifted to brand blue family
    prescriptionPrimary = MedsyBlue,
    prescriptionTitle = PrimaryText,
    prescriptionSupporting = SecondaryText,
    prescriptionSuccessContainer = Color(0xFFEEFFF4),
    prescriptionSuccessSoft = Color(0xFFE5F4ED),
    prescriptionSuccessContent = Color(0xFF16A34A),
    prescriptionWarningContainer = Color(0xFFFFFBEB),
    prescriptionWarningContent = Color(0xFF92400E),
    prescriptionWarningBorder = Color(0xFFFDE68A),
    prescriptionErrorContainer = Color(0xFFFEF2F2),
    prescriptionErrorContent = ErrorRed,
    prescriptionDisabledContainer = MedsyBlueSurface,
    prescriptionGalleryContainer = Color(0xFFEFF3FF),
    prescriptionGalleryContent = MedsyBlue,
    prescriptionScanBorder = MedsyBlueBorder,
)

// ─── Dark extended colours ────────────────────────────────────────────────────
internal val darkExtendedColors = ExtendedColors(
    success = SuccessGreen,
    onSuccess = Color.White,
    // Category chips
    blueContainer = Color(0xFF0D2A5E),
    blueContent = Color(0xFF93C5FD),
    orangeContainer = Color(0xFF431407),
    orangeContent = Color(0xFFFB923C),
    pinkContainer = Color(0xFF500724),
    pinkContent = Color(0xFFF9A8D4),
    purpleContainer = Color(0xFF2E1065),
    purpleContent = Color(0xFFD8B4FE),
    neutralContainer = Color(0xFF1F2937),
    neutralContent = Color(0xFF9CA3AF),

    categoryMoreBg = Color(0xFF1C2A45),
    categoryMoreIcon = Color(0xFF93C5FD),

    // Prescription — dark-mode blue family
    prescriptionPrimary = Color(0xFF60A5FA),
    prescriptionTitle = Color(0xFFEFF6FF),
    prescriptionSupporting = Color(0xFF93C5FD),
    prescriptionSuccessContainer = Color(0xFF052E16),
    prescriptionSuccessSoft = Color(0xFF0D3321),
    prescriptionSuccessContent = Color(0xFF86EFAC),
    prescriptionWarningContainer = Color(0xFF422006),
    prescriptionWarningContent = Color(0xFFFDE68A),
    prescriptionWarningBorder = Color(0xFF854D0E),
    prescriptionErrorContainer = Color(0xFF450A0A),
    prescriptionErrorContent = Color(0xFFFCA5A5),
    prescriptionDisabledContainer = Color(0xFF1C2A45),
    prescriptionGalleryContainer = Color(0xFF0D2A5E),
    prescriptionGalleryContent = Color(0xFF93C5FD),
    prescriptionScanBorder = Color(0xFF2A3F6A),
)
