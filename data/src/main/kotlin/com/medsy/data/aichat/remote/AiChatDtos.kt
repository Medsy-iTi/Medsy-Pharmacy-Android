package com.medsy.data.aichat.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatMessageRequestDto(
    val message: String,
    val analyticsPreset: String? = null,
)

@JsonClass(generateAdapter = true)
data class ChatMessageResponseDto(
    val messageId: Long? = null,
    val intent: String? = null,
    val answer: String? = null,
    val products: List<ChatProductDto>? = null,
    val alternatives: List<ChatProductDto>? = null,
    val doctorSpecializations: List<String>? = null,
    val emergencyNumbers: List<EmergencyNumberDto>? = null,
    val categories: List<ChatCategoryDto>? = null,
    val pharmacistRankings: List<PharmacistRankingDto>? = null,
    val analytics: ChatAnalyticsDto? = null,
    val disclaimer: String? = null,
)

@JsonClass(generateAdapter = true)
data class ChatHistoryResponseDto(
    val conversationId: Long? = null,
    val messages: List<ChatHistoryMessageDto>? = null,
)

@JsonClass(generateAdapter = true)
data class ChatHistoryMessageDto(
    val id: Long? = null,
    val role: String? = null,
    val content: String? = null,
    val intent: String? = null,
    val products: List<ChatProductDto>? = null,
    val alternatives: List<ChatProductDto>? = null,
    val doctorSpecializations: List<String>? = null,
    val emergencyNumbers: List<EmergencyNumberDto>? = null,
    val categories: List<ChatCategoryDto>? = null,
    val pharmacistRankings: List<PharmacistRankingDto>? = null,
    val analytics: ChatAnalyticsDto? = null,
)

@JsonClass(generateAdapter = true)
data class ChatProductDto(
    val id: Long? = null,
    val name: String? = null,
    val productName: String? = null,
    val scientificName: String? = null,
    val strength: String? = null,
    val packSize: String? = null,
    val form: String? = null,
    val price: Double? = null,
    val company: String? = null,
    val route: String? = null,
    val description: String? = null,
    val imageUrl: String? = null,
)

@JsonClass(generateAdapter = true)
data class EmergencyNumberDto(val service: String? = null, val number: String? = null)

@JsonClass(generateAdapter = true)
data class ChatCategoryDto(val id: Long? = null, val name: String? = null)

@JsonClass(generateAdapter = true)
data class PharmacistRankingDto(
    val metric: String? = null,
    val period: String? = null,
    val direction: String? = null,
    val entries: List<PharmacistPerformanceEntryDto>? = null,
)

@JsonClass(generateAdapter = true)
data class PharmacistPerformanceEntryDto(
    val rank: Int? = null,
    val pharmacistId: Long? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val count: Long? = null,
)

@JsonClass(generateAdapter = true)
data class ChatAnalyticsDto(
    val schemaVersion: Int? = null,
    val scope: String? = null,
    val period: String? = null,
    val start: String? = null,
    val end: String? = null,
    val metrics: List<AnalyticsMetricDto>? = null,
    val breakdowns: List<AnalyticsBreakdownDto>? = null,
    val rankings: List<AnalyticsRankingEntryDto>? = null,
    val orderHighlights: List<AnalyticsOrderHighlightDto>? = null,
    val topProducts: List<AnalyticsTopProductDto>? = null,
)

@JsonClass(generateAdapter = true)
data class AnalyticsMetricDto(
    val key: String? = null,
    val value: Double? = null,
    val unit: String? = null,
    val previousValue: Double? = null,
    val deltaPercent: Double? = null,
)

@JsonClass(generateAdapter = true)
data class AnalyticsBreakdownDto(
    val group: String? = null,
    val key: String? = null,
    val count: Long? = null,
)

@JsonClass(generateAdapter = true)
data class AnalyticsRankingEntryDto(
    val rank: Int? = null,
    val pharmacistId: Long? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val count: Long? = null,
)

@JsonClass(generateAdapter = true)
data class AnalyticsOrderHighlightDto(
    val orderId: Long? = null,
    val status: String? = null,
    val totalPrice: Double? = null,
    val date: String? = null,
)

@JsonClass(generateAdapter = true)
data class AnalyticsTopProductDto(
    val productId: Long? = null,
    val productName: String? = null,
    val quantity: Long? = null,
    val orderCount: Long? = null,
    val revenue: Double? = null,
)
