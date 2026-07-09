package com.example.stackoverflow.core.network.mapper

import com.example.stackoverflow.core.network.model.Answer
import com.example.stackoverflow.core.network.model.Question
import com.example.stackoverflow.core.network.remote.dto.AnswerDto
import com.example.stackoverflow.core.network.remote.dto.QuestionDto

fun QuestionDto.toDomain(): Question = Question(
    id = questionId,
    title = title,
    bodyHtml = body.orEmpty(),
    tags = tags,
    ownerName = owner?.displayName ?: "Anonymous",
    ownerAvatarUrl = owner?.profileImage,
    ownerReputation = owner?.reputation ?: 0,
    isAnswered = isAnswered,
    viewCount = viewCount,
    answerCount = answerCount,
    score = score,
    createdAtEpochSeconds = creationDate,
    link = link,
)

fun AnswerDto.toDomain(): Answer = Answer(
    id = answerId,
    questionId = questionId,
    bodyHtml = body.orEmpty(),
    ownerName = owner?.displayName ?: "Anonymous",
    ownerAvatarUrl = owner?.profileImage,
    ownerReputation = owner?.reputation ?: 0,
    isAccepted = isAccepted,
    score = score,
    createdAtEpochSeconds = creationDate,
)
