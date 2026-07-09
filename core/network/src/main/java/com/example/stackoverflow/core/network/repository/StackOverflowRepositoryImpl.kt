package com.example.stackoverflow.core.network.repository

import com.example.stackoverflow.core.network.mapper.toDomain
import com.example.stackoverflow.core.network.model.Answer
import com.example.stackoverflow.core.network.model.AnswerSort
import com.example.stackoverflow.core.network.model.Question
import com.example.stackoverflow.core.network.remote.StackExchangeApi
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StackOverflowRepositoryImpl @Inject constructor(
    private val api: StackExchangeApi,
) : StackOverflowRepository {

    private val questionCache = ConcurrentHashMap<Long, Question>()

    override suspend fun searchQuestions(query: String): Result<List<Question>> = runCatching {
        api.searchQuestions(title = query).items.map { it.toDomain() }.also { questions ->
            questions.forEach { questionCache[it.id] = it }
        }
    }

    override suspend fun getAnswers(questionId: Long, sort: AnswerSort): Result<List<Answer>> = runCatching {
        api.getAnswers(questionId, sort = sort.apiSort, order = sort.apiOrder).items.map { it.toDomain() }
    }

    override fun getCachedQuestion(questionId: Long): Question? = questionCache[questionId]
}
