package com.medsy.domain.aichat.model

data class AiChatSession(
    val messages: List<AiChatMessage> = emptyList(),
    val isResponding: Boolean = false,
    val isHydrated: Boolean = false,
)

data class AiChatMessage(
    val id: Long,
    val sender: AiChatSender,
    val content: AiChatContent,
)

enum class AiChatSender { USER, ASSISTANT }

sealed interface AiChatContent {
    data class UserText(
        val value: String,
        val imageUri: String? = null,
    ) : AiChatContent

    data class AssistantMessage(
        val answer: String,
        val intent: AiChatIntent,
        val products: List<AiCatalogProduct>,
        val doctorSpecializations: List<String>,
        val emergencyNumbers: List<AiEmergencyNumber>,
        val categories: List<AiChatCategory>,
        val pharmacistRankings: List<AiPharmacistRanking>,
        val analytics: AiChatAnalytics?,
        val disclaimer: String?,
    ) : AiChatContent
}

enum class AiChatIntent {
    GREETING,
    MEDICINE_REQUEST,
    SYMPTOM_ADVICE,
    DOCTOR_SPECIALIZATION,
    EMERGENCY,
    MEDICINE_USAGE,
    CATEGORY_BROWSE,
    PHARMACIST_PERFORMANCE,
    PHARMACY_ANALYTICS,
    OTHER,
}

data class AiChatImage(val uri: String, val storageKey: String)

sealed interface AiChatOutgoingMessage {
    data class Text(
        val message: String,
        val analyticsPreset: AiAnalyticsPreset? = null,
    ) : AiChatOutgoingMessage
    data class Image(val image: AiChatImage, val caption: String) : AiChatOutgoingMessage
}

data class AiEmergencyNumber(val service: AiEmergencyService, val number: String)
enum class AiEmergencyService { AMBULANCE, POLICE, FIRE }
data class AiChatCategory(val id: Int, val name: String)

data class AiCatalogProduct(
    val productId: Int,
    val name: String,
    val scientificName: String?,
    val strength: String?,
    val packSize: String?,
    val form: String?,
    val priceEgp: Double?,
    val company: String?,
    val route: String?,
    val description: String?,
    val imageUrl: String?,
)

data class AiPharmacistRanking(
    val metric: AiPerformanceMetric,
    val period: AiPerformancePeriod,
    val direction: AiPerformanceDirection,
    val entries: List<AiPharmacistPerformanceEntry>,
)

data class AiPharmacistPerformanceEntry(
    val rank: Int,
    val pharmacistId: Long,
    val firstName: String,
    val lastName: String,
    val count: Long,
) {
    val fullName: String get() = "$firstName $lastName".trim()
}

enum class AiPerformanceMetric { OFFERS_CREATED, SUCCESSFUL_ORDERS }
enum class AiPerformancePeriod { LAST_DAY, LAST_WEEK, LAST_MONTH, LAST_YEAR }
enum class AiPerformanceDirection { TOP, BOTTOM }

enum class AiAnalyticsPreset {
    PHARMACY_MONTH_OVERVIEW,
    PHARMACY_MONTH_ACCEPTANCE,
    PHARMACY_MONTH_TOP_EMPLOYEE,
    PHARMACY_MONTH_LARGEST_ORDER,
    SELF_MONTH_OVERVIEW,
    SELF_MONTH_ORDERS,
}

data class AiChatAnalytics(
    val schemaVersion: Int,
    val scope: String,
    val period: String,
    val start: String,
    val end: String,
    val metrics: List<AiAnalyticsMetric>,
    val breakdowns: List<AiAnalyticsBreakdown>,
    val rankings: List<AiAnalyticsRankingEntry>,
    val orderHighlights: List<AiAnalyticsOrderHighlight>,
    val topProducts: List<AiAnalyticsTopProduct>,
)

data class AiAnalyticsMetric(
    val key: String,
    val value: Double,
    val unit: String,
    val previousValue: Double?,
    val deltaPercent: Double?,
)

data class AiAnalyticsBreakdown(val group: String, val key: String, val count: Long)

data class AiAnalyticsRankingEntry(
    val rank: Int,
    val pharmacistId: Long,
    val firstName: String,
    val lastName: String,
    val count: Long,
) {
    val fullName: String get() = "$firstName $lastName".trim()
}

data class AiAnalyticsOrderHighlight(
    val orderId: Long,
    val status: String,
    val totalPrice: Double,
    val date: String,
)

data class AiAnalyticsTopProduct(
    val productId: Long,
    val productName: String,
    val quantity: Long,
    val orderCount: Long,
    val revenue: Double,
)
