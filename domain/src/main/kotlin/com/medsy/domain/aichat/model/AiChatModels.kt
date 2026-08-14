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
    OTHER,
}

data class AiChatImage(val uri: String, val storageKey: String)

sealed interface AiChatOutgoingMessage {
    data class Text(val message: String) : AiChatOutgoingMessage
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
