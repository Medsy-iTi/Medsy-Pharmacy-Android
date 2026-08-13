package com.medsy.presentation.orderdetails

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.medsy.presentation.R
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
internal fun requestAgeLabel(createdAt: String): String? {
    if (!createdAt.contains('T')) return null
    val minutes = runCatching {
        val normalized = if (createdAt.endsWith("Z")) createdAt else "${createdAt}Z"
        Duration.between(Instant.parse(normalized), Instant.now())
            .toMinutes()
            .coerceAtLeast(0)
            .toInt()
    }.getOrNull() ?: return null

    return if (minutes >= 60) {
        stringResource(R.string.request_details_hours_minutes_ago, minutes / 60, minutes % 60)
    } else {
        stringResource(R.string.request_details_minutes_ago, minutes)
    }
}

@Composable
internal fun orderDateLabel(createdAt: String?): String? {
    val raw = createdAt?.takeIf(String::isNotBlank) ?: return null
    val locale = LocalConfiguration.current.locales[0]
    return runCatching {
        LocalDate.parse(raw.substringBefore('T')).format(
            DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale),
        )
    }.getOrElse { raw }
}
