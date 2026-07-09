package com.example.stackoverflow.feature.detail

import com.example.stackoverflow.core.network.model.Answer
import com.example.stackoverflow.core.network.model.AnswerSort
import com.example.stackoverflow.core.network.model.Question

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Success(
        val question: Question,
        val answers: List<Answer>,
        val sort: AnswerSort,
    ) : DetailUiState
    data class Error(val message: String) : DetailUiState
    data object NoNetwork : DetailUiState
}
