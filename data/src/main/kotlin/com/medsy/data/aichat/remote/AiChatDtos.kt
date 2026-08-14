package com.medsy.data.aichat.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatMessageRequestDto(val message: String)

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
