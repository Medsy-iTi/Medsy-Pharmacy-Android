package com.medsy.data.aichat.mapper

import com.medsy.data.aichat.remote.ChatCategoryDto
import com.medsy.data.aichat.remote.ChatHistoryMessageDto
import com.medsy.data.aichat.remote.ChatMessageResponseDto
import com.medsy.data.aichat.remote.ChatProductDto
import com.medsy.data.aichat.remote.ChatAnalyticsDto
import com.medsy.data.aichat.remote.AnalyticsBreakdownDto
import com.medsy.data.aichat.remote.AnalyticsMetricDto
import com.medsy.data.aichat.remote.AnalyticsOrderHighlightDto
import com.medsy.data.aichat.remote.AnalyticsRankingEntryDto
import com.medsy.data.aichat.remote.AnalyticsTopProductDto
import com.medsy.data.aichat.remote.EmergencyNumberDto
import com.medsy.data.aichat.remote.PharmacistPerformanceEntryDto
import com.medsy.data.aichat.remote.PharmacistRankingDto
import com.medsy.domain.aichat.model.AiCatalogProduct
import com.medsy.domain.aichat.model.AiChatAnalytics
import com.medsy.domain.aichat.model.AiAnalyticsBreakdown
import com.medsy.domain.aichat.model.AiAnalyticsMetric
import com.medsy.domain.aichat.model.AiAnalyticsOrderHighlight
import com.medsy.domain.aichat.model.AiAnalyticsRankingEntry
import com.medsy.domain.aichat.model.AiAnalyticsTopProduct
import com.medsy.domain.aichat.model.AiChatCategory
import com.medsy.domain.aichat.model.AiChatContent
import com.medsy.domain.aichat.model.AiChatIntent
import com.medsy.domain.aichat.model.AiChatMessage
import com.medsy.domain.aichat.model.AiChatSender
import com.medsy.domain.aichat.model.AiEmergencyNumber
import com.medsy.domain.aichat.model.AiEmergencyService
import com.medsy.domain.aichat.model.AiPerformanceDirection
import com.medsy.domain.aichat.model.AiPerformanceMetric
import com.medsy.domain.aichat.model.AiPerformancePeriod
import com.medsy.domain.aichat.model.AiPharmacistPerformanceEntry
import com.medsy.domain.aichat.model.AiPharmacistRanking

private val IMAGE_MARKER_REGEX = Regex("""\n?\[image][^\n]*""")

fun ChatMessageResponseDto.toDomain() = AiChatContent.AssistantMessage(
    answer = answer?.trim().orEmpty(),
    intent = intent.toIntent(),
    products = (products.orEmpty() + alternatives.orEmpty())
        .mapNotNull(ChatProductDto::toDomain)
        .distinctBy(AiCatalogProduct::productId),
    doctorSpecializations = doctorSpecializations.orEmpty().filter(String::isNotBlank),
    emergencyNumbers = emergencyNumbers.orEmpty().mapNotNull(EmergencyNumberDto::toDomain),
    categories = categories.orEmpty().mapNotNull(ChatCategoryDto::toDomain),
    pharmacistRankings = pharmacistRankings.orEmpty().mapNotNull(PharmacistRankingDto::toDomain),
    analytics = analytics?.toDomain(),
    disclaimer = disclaimer?.takeIf(String::isNotBlank),
)

fun ChatHistoryMessageDto.toDomain(): AiChatMessage? {
    val messageId = id ?: return null
    return when (role?.uppercase()) {
        "USER" -> AiChatMessage(
            id = messageId,
            sender = AiChatSender.USER,
            content = AiChatContent.UserText(
                content.orEmpty().replace(IMAGE_MARKER_REGEX, "").trim(),
            ),
        )
        "ASSISTANT" -> AiChatMessage(
            id = messageId,
            sender = AiChatSender.ASSISTANT,
            content = AiChatContent.AssistantMessage(
                answer = content?.trim().orEmpty(),
                intent = intent.toIntent(),
                products = (products.orEmpty() + alternatives.orEmpty())
                    .mapNotNull(ChatProductDto::toDomain)
                    .distinctBy(AiCatalogProduct::productId),
                doctorSpecializations = doctorSpecializations.orEmpty().filter(String::isNotBlank),
                emergencyNumbers = emergencyNumbers.orEmpty().mapNotNull(EmergencyNumberDto::toDomain),
                categories = categories.orEmpty().mapNotNull(ChatCategoryDto::toDomain),
                pharmacistRankings = pharmacistRankings.orEmpty()
                    .mapNotNull(PharmacistRankingDto::toDomain),
                analytics = analytics?.toDomain(),
                disclaimer = null,
            ),
        )
        else -> null
    }
}

private fun String?.toIntent(): AiChatIntent = this?.trim()?.uppercase()
    ?.let { value -> AiChatIntent.entries.firstOrNull { it.name == value } }
    ?: AiChatIntent.OTHER

private fun ChatProductDto.toDomain(): AiCatalogProduct? {
    val resolvedId = id?.takeIf { it in 1L..Int.MAX_VALUE.toLong() }?.toInt() ?: return null
    val displayName = productName?.takeIf(String::isNotBlank)
        ?: name?.takeIf(String::isNotBlank)
        ?: return null
    return AiCatalogProduct(
        productId = resolvedId,
        name = displayName,
        scientificName = scientificName?.takeIf(String::isNotBlank),
        strength = strength?.takeIf(String::isNotBlank),
        packSize = packSize?.takeIf(String::isNotBlank),
        form = form?.takeIf(String::isNotBlank),
        priceEgp = price,
        company = company?.takeIf(String::isNotBlank),
        route = route?.takeIf(String::isNotBlank),
        description = description?.takeIf(String::isNotBlank),
        imageUrl = imageUrl?.takeIf(String::isNotBlank),
    )
}

private fun EmergencyNumberDto.toDomain(): AiEmergencyNumber? {
    val value = number?.takeIf(String::isNotBlank) ?: return null
    val type = when (service?.uppercase()) {
        "AMBULANCE" -> AiEmergencyService.AMBULANCE
        "POLICE" -> AiEmergencyService.POLICE
        "FIRE" -> AiEmergencyService.FIRE
        else -> return null
    }
    return AiEmergencyNumber(type, value)
}

private fun ChatCategoryDto.toDomain(): AiChatCategory? {
    val categoryId = id?.takeIf { it in 1L..Int.MAX_VALUE.toLong() }?.toInt() ?: return null
    return AiChatCategory(categoryId, name?.takeIf(String::isNotBlank) ?: return null)
}

private fun PharmacistRankingDto.toDomain(): AiPharmacistRanking? {
    val resolvedMetric = runCatching { AiPerformanceMetric.valueOf(metric.orEmpty()) }.getOrNull()
        ?: return null
    val resolvedPeriod = runCatching { AiPerformancePeriod.valueOf(period.orEmpty()) }.getOrNull()
        ?: return null
    val resolvedDirection = runCatching {
        AiPerformanceDirection.valueOf(direction.orEmpty())
    }.getOrNull() ?: return null
    return AiPharmacistRanking(
        metric = resolvedMetric,
        period = resolvedPeriod,
        direction = resolvedDirection,
        entries = entries.orEmpty().mapNotNull(PharmacistPerformanceEntryDto::toDomain),
    )
}

private fun PharmacistPerformanceEntryDto.toDomain(): AiPharmacistPerformanceEntry? =
    AiPharmacistPerformanceEntry(
        rank = rank?.takeIf { it > 0 } ?: return null,
        pharmacistId = pharmacistId?.takeIf { it > 0 } ?: return null,
        firstName = firstName?.takeIf(String::isNotBlank) ?: return null,
        lastName = lastName.orEmpty(),
        count = count?.takeIf { it >= 0 } ?: return null,
    )

private fun ChatAnalyticsDto.toDomain(): AiChatAnalytics? {
    val resolvedScope = scope?.takeIf(String::isNotBlank) ?: return null
    val resolvedPeriod = period?.takeIf(String::isNotBlank) ?: return null
    val resolvedStart = start?.takeIf(String::isNotBlank) ?: return null
    val resolvedEnd = end?.takeIf(String::isNotBlank) ?: return null
    return AiChatAnalytics(
        schemaVersion = schemaVersion?.takeIf { it > 0 } ?: 1,
        scope = resolvedScope,
        period = resolvedPeriod,
        start = resolvedStart,
        end = resolvedEnd,
        metrics = metrics.orEmpty().mapNotNull(AnalyticsMetricDto::toDomain),
        breakdowns = breakdowns.orEmpty().mapNotNull(AnalyticsBreakdownDto::toDomain),
        rankings = rankings.orEmpty().mapNotNull(AnalyticsRankingEntryDto::toDomain),
        orderHighlights = orderHighlights.orEmpty().mapNotNull(AnalyticsOrderHighlightDto::toDomain),
        topProducts = topProducts.orEmpty().mapNotNull(AnalyticsTopProductDto::toDomain),
    )
}

private fun AnalyticsMetricDto.toDomain(): AiAnalyticsMetric? = AiAnalyticsMetric(
    key = key?.takeIf(String::isNotBlank) ?: return null,
    value = value ?: return null,
    unit = unit?.takeIf(String::isNotBlank) ?: "COUNT",
    previousValue = previousValue,
    deltaPercent = deltaPercent,
)

private fun AnalyticsBreakdownDto.toDomain(): AiAnalyticsBreakdown? = AiAnalyticsBreakdown(
    group = group?.takeIf(String::isNotBlank) ?: return null,
    key = key?.takeIf(String::isNotBlank) ?: return null,
    count = count?.takeIf { it >= 0 } ?: return null,
)

private fun AnalyticsRankingEntryDto.toDomain(): AiAnalyticsRankingEntry? = AiAnalyticsRankingEntry(
    rank = rank?.takeIf { it > 0 } ?: return null,
    pharmacistId = pharmacistId?.takeIf { it > 0 } ?: return null,
    firstName = firstName?.takeIf(String::isNotBlank) ?: return null,
    lastName = lastName.orEmpty(),
    count = count?.takeIf { it >= 0 } ?: return null,
)

private fun AnalyticsOrderHighlightDto.toDomain(): AiAnalyticsOrderHighlight? =
    AiAnalyticsOrderHighlight(
        orderId = orderId?.takeIf { it > 0 } ?: return null,
        status = status?.takeIf(String::isNotBlank) ?: return null,
        totalPrice = totalPrice ?: return null,
        date = date?.takeIf(String::isNotBlank) ?: return null,
    )

private fun AnalyticsTopProductDto.toDomain(): AiAnalyticsTopProduct? = AiAnalyticsTopProduct(
    productId = productId?.takeIf { it > 0 } ?: return null,
    productName = productName?.takeIf(String::isNotBlank) ?: return null,
    quantity = quantity?.takeIf { it >= 0 } ?: return null,
    orderCount = orderCount?.takeIf { it >= 0 } ?: return null,
    revenue = revenue ?: return null,
)
