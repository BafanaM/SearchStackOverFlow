package com.example.stackoverflow.feature.search

import com.example.stackoverflow.core.network.repository.RecentSearchesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeRecentSearchesRepository : RecentSearchesRepository {

    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    override val recentSearches: Flow<List<String>> = _recentSearches.asStateFlow()

    var clearCalled: Boolean = false
        private set

    val currentValue: List<String>
        get() = _recentSearches.value

    override suspend fun addSearch(query: String) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return
        _recentSearches.value = listOf(trimmed) + _recentSearches.value.filterNot { it.equals(trimmed, ignoreCase = true) }
    }

    override suspend fun clear() {
        clearCalled = true
        _recentSearches.value = emptyList()
    }
}
