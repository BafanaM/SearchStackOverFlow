package com.example.stackoverflow.feature.detail

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.example.stackoverflow.core.network.model.Answer
import com.example.stackoverflow.core.network.model.AnswerSort
import com.example.stackoverflow.core.network.model.Question
import com.example.stackoverflow.core.network.repository.StackOverflowRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val questionId = 42L

    private lateinit var repository: StackOverflowRepository
    private lateinit var networkMonitor: FakeNetworkMonitor
    private lateinit var context: Context
    private lateinit var question: Question

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        networkMonitor = FakeNetworkMonitor(initiallyOnline = true)
        context = mockk {
            every { getString(R.string.detail_question_not_found) } returns "Question not found"
            every { getString(R.string.detail_generic_error) } returns "Something went wrong"
        }
        question = sampleQuestion(questionId)
        every { repository.getCachedQuestion(questionId) } returns question
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): DetailViewModel {
        val savedStateHandle = SavedStateHandle(mapOf("questionId" to questionId))
        return DetailViewModel(savedStateHandle, repository, networkMonitor, context)
    }

    @Test
    fun `question not cached shows Error`() = runTest {
        every { repository.getCachedQuestion(questionId) } returns null

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(DetailUiState.Error("Question not found"), viewModel.uiState.value)
    }

    @Test
    fun `offline shows NoNetwork without calling repository`() = runTest {
        networkMonitor.setOnline(false)

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(DetailUiState.NoNetwork, viewModel.uiState.value)
        coVerify(exactly = 0) { repository.getAnswers(any(), any()) }
    }

    @Test
    fun `loads answers with Active sort on init, emitting Loading then Success`() = runTest {
        val answers = listOf(sampleAnswer())
        coEvery { repository.getAnswers(questionId, AnswerSort.ACTIVE) } returns Result.success(answers)

        val viewModel = createViewModel()

        viewModel.uiState.test {
            assertEquals(DetailUiState.Loading, awaitItem())
            assertEquals(DetailUiState.Success(question, answers, AnswerSort.ACTIVE), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `answers failure with IOException shows NoNetwork`() = runTest {
        coEvery { repository.getAnswers(questionId, AnswerSort.ACTIVE) } returns Result.failure(IOException())

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(DetailUiState.NoNetwork, viewModel.uiState.value)
    }

    @Test
    fun `answers failure with other exception shows Error with its message`() = runTest {
        coEvery { repository.getAnswers(questionId, AnswerSort.ACTIVE) } returns Result.failure(RuntimeException("oops"))

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(DetailUiState.Error("oops"), viewModel.uiState.value)
    }

    @Test
    fun `answers failure with no message falls back to generic string resource`() = runTest {
        coEvery { repository.getAnswers(questionId, AnswerSort.ACTIVE) } returns Result.failure(RuntimeException())

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(DetailUiState.Error("Something went wrong"), viewModel.uiState.value)
    }

    @Test
    fun `onSortSelected reloads answers with new sort`() = runTest {
        coEvery { repository.getAnswers(questionId, AnswerSort.ACTIVE) } returns Result.success(emptyList())
        val sortedAnswers = listOf(sampleAnswer())
        coEvery { repository.getAnswers(questionId, AnswerSort.VOTES) } returns Result.success(sortedAnswers)

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onSortSelected(AnswerSort.VOTES)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(DetailUiState.Success(question, sortedAnswers, AnswerSort.VOTES), viewModel.uiState.value)
        coVerify { repository.getAnswers(questionId, AnswerSort.VOTES) }
    }

    @Test
    fun `retry reuses the last successful sort`() = runTest {
        coEvery { repository.getAnswers(questionId, AnswerSort.ACTIVE) } returns Result.success(emptyList())
        coEvery { repository.getAnswers(questionId, AnswerSort.OLDEST) } returns Result.success(emptyList())

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.onSortSelected(AnswerSort.OLDEST)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.retry()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 2) { repository.getAnswers(questionId, AnswerSort.OLDEST) }
    }

    @Test
    fun `retry falls back to Active sort when not currently Success`() = runTest {
        networkMonitor.setOnline(false)
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(DetailUiState.NoNetwork, viewModel.uiState.value)

        networkMonitor.setOnline(true)
        coEvery { repository.getAnswers(questionId, AnswerSort.ACTIVE) } returns Result.success(emptyList())
        viewModel.retry()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value is DetailUiState.Success)
        coVerify { repository.getAnswers(questionId, AnswerSort.ACTIVE) }
    }

    private fun sampleQuestion(id: Long) = Question(
        id = id,
        title = "title",
        bodyHtml = "body",
        tags = emptyList(),
        ownerName = "owner",
        ownerAvatarUrl = null,
        ownerReputation = 0,
        isAnswered = false,
        viewCount = 0,
        answerCount = 0,
        score = 0,
        createdAtEpochSeconds = 0,
        link = null,
    )

    private fun sampleAnswer() = Answer(
        id = 1L,
        questionId = questionId,
        bodyHtml = "answer body",
        ownerName = "owner",
        ownerAvatarUrl = null,
        ownerReputation = 0,
        isAccepted = false,
        score = 0,
        createdAtEpochSeconds = 0,
    )
}
