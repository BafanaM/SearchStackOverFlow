package com.example.stackoverflow.feature.detail

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stackoverflow.core.network.connectivity.NetworkMonitor
import com.example.stackoverflow.core.network.model.AnswerSort
import com.example.stackoverflow.core.network.repository.StackOverflowRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: StackOverflowRepository,
    private val networkMonitor: NetworkMonitor,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val questionId: Long = checkNotNull(savedStateHandle["questionId"])

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        loadAnswers(AnswerSort.ACTIVE)
    }

    fun onSortSelected(sort: AnswerSort) {
        loadAnswers(sort)
    }

    fun retry() {
        val currentSort = (_uiState.value as? DetailUiState.Success)?.sort ?: AnswerSort.ACTIVE
        loadAnswers(currentSort)
    }

    private fun loadAnswers(sort: AnswerSort) {
        val question = repository.getCachedQuestion(questionId)
        if (question == null) {
            _uiState.value = DetailUiState.Error(context.getString(R.string.detail_question_not_found))
            return
        }

        if (!networkMonitor.isOnline.value) {
            _uiState.value = DetailUiState.NoNetwork
            return
        }

        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            repository.getAnswers(questionId, sort)
                .onSuccess { answers ->
                    _uiState.value = DetailUiState.Success(question, answers, sort)
                }
                .onFailure { error ->
                    _uiState.value = if (error is IOException) {
                        DetailUiState.NoNetwork
                    } else {
                        DetailUiState.Error(error.message ?: context.getString(R.string.detail_generic_error))
                    }
                }
        }
    }
}
