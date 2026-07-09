package com.example.stackoverflow.feature.search

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stackoverflow.core.network.connectivity.NetworkMonitor
import com.example.stackoverflow.core.network.repository.RecentSearchesRepository
import com.example.stackoverflow.core.network.repository.StackOverflowRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: StackOverflowRepository,
    private val recentSearchesRepository: RecentSearchesRepository,
    networkMonitor: NetworkMonitor,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    val isOnline: StateFlow<Boolean> = networkMonitor.isOnline

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    val recentSearches: StateFlow<List<String>> = recentSearchesRepository.recentSearches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        if (!isOnline.value) {
            _uiState.value = SearchUiState.NoNetwork
        }
    }

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
        if (newQuery.isBlank()) {
            _uiState.value = SearchUiState.Idle
        }
    }

    fun onRecentSearchClick(recentQuery: String) {
        _query.value = recentQuery
        search()
    }

    fun onClearRecentSearches() {
        viewModelScope.launch {
            recentSearchesRepository.clear()
        }
    }

    fun search() {
        val trimmedQuery = _query.value.trim()
        if (trimmedQuery.isEmpty()) return

        if (!isOnline.value) {
            _uiState.value = SearchUiState.NoNetwork
            return
        }

        viewModelScope.launch {
            _uiState.value = SearchUiState.Loading
            recentSearchesRepository.addSearch(trimmedQuery)
            repository.searchQuestions(trimmedQuery)
                .onSuccess { questions -> _uiState.value = SearchUiState.Success(questions) }
                .onFailure { error ->
                    _uiState.value = if (error is IOException) {
                        SearchUiState.NoNetwork
                    } else {
                        SearchUiState.Error(error.message ?: context.getString(R.string.search_generic_error))
                    }
                }
        }
    }

    fun retry() {
        if (_query.value.isBlank()) {
            _uiState.value = if (isOnline.value) SearchUiState.Idle else SearchUiState.NoNetwork
        } else {
            search()
        }
    }

    fun dismissNoNetwork() {
        _uiState.value = SearchUiState.Idle
    }
}
