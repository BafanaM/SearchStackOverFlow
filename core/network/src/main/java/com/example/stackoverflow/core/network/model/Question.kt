package com.example.stackoverflow.core.network.model

data class Question(
    val id: Long,
    val title: String,
    val bodyHtml: String,
    val tags: List<String>,
    val ownerName: String,
    val ownerAvatarUrl: String?,
    val ownerReputation: Long,
    val isAnswered: Boolean,
    val viewCount: Long,
    val answerCount: Int,
    val score: Int,
    val createdAtEpochSeconds: Long,
    val link: String?,
)
