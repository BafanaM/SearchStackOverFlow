package com.example.stackoverflow.core.network.remote

import com.example.stackoverflow.core.network.remote.dto.AnswersResponseDto
import com.example.stackoverflow.core.network.remote.dto.SearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface StackExchangeApi {

    @GET("search/advanced")
    suspend fun searchQuestions(
        @Query("title") title: String,
        @Query("pagesize") pageSize: Int = 20,
        @Query("order") order: String = "desc",
        @Query("sort") sort: String = "activity",
        @Query("site") site: String = "stackoverflow",
        @Query("filter") filter: String = "withbody",
    ): SearchResponseDto

    @GET("questions/{questionId}/answers")
    suspend fun getAnswers(
        @Path("questionId") questionId: Long,
        @Query("order") order: String = "desc",
        @Query("sort") sort: String = "activity",
        @Query("site") site: String = "stackoverflow",
        @Query("filter") filter: String = "withbody",
    ): AnswersResponseDto

    companion object {
        const val BASE_URL = "https://api.stackexchange.com/2.2/"
    }
}
