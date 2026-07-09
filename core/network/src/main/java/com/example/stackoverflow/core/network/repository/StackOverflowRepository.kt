package com.example.stackoverflow.core.network.repository

import com.example.stackoverflow.core.network.model.Answer
import com.example.stackoverflow.core.network.model.AnswerSort
import com.example.stackoverflow.core.network.model.Question

interface StackOverflowRepository {

    suspend fun searchQuestions(query: String): Result<List<Question>>

    suspend fun getAnswers(questionId: Long, sort: AnswerSort): Result<List<Answer>>

    fun getCachedQuestion(questionId: Long): Question?
}
