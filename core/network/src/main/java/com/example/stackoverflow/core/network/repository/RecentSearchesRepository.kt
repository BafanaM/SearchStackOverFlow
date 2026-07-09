package com.example.stackoverflow.core.network.repository

import kotlinx.coroutines.flow.Flow

interface RecentSearchesRepository {

    val recentSearches: Flow<List<String>>

    suspend fun addSearch(query: String)

    suspend fun clear()
}
