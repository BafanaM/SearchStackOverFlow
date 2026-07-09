package com.example.stackoverflow.core.network.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResponseDto(
    @SerialName("items") val items: List<QuestionDto> = emptyList(),
    @SerialName("has_more") val hasMore: Boolean = false,
)

@Serializable
data class QuestionDto(
    @SerialName("question_id") val questionId: Long,
    @SerialName("title") val title: String = "",
    @SerialName("body") val body: String? = null,
    @SerialName("tags") val tags: List<String> = emptyList(),
    @SerialName("owner") val owner: OwnerDto? = null,
    @SerialName("is_answered") val isAnswered: Boolean = false,
    @SerialName("view_count") val viewCount: Long = 0,
    @SerialName("answer_count") val answerCount: Int = 0,
    @SerialName("score") val score: Int = 0,
    @SerialName("creation_date") val creationDate: Long = 0,
    @SerialName("link") val link: String? = null,
)
