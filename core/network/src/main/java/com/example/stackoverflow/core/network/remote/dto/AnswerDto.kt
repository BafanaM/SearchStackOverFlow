package com.example.stackoverflow.core.network.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnswersResponseDto(
    @SerialName("items") val items: List<AnswerDto> = emptyList(),
    @SerialName("has_more") val hasMore: Boolean = false,
)

@Serializable
data class AnswerDto(
    @SerialName("answer_id") val answerId: Long,
    @SerialName("question_id") val questionId: Long = 0,
    @SerialName("body") val body: String? = null,
    @SerialName("owner") val owner: OwnerDto? = null,
    @SerialName("is_accepted") val isAccepted: Boolean = false,
    @SerialName("score") val score: Int = 0,
    @SerialName("creation_date") val creationDate: Long = 0,
)
