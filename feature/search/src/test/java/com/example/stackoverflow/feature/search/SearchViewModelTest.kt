package com.example.stackoverflow.feature.search

import android.content.Context
import app.cash.turbine.test
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
class SearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var repository: StackOverflowRepository
    private lateinit var recentSearchesRepository: FakeRecentSearchesRepository
    private lateinit var networkMonitor: FakeNetworkMonitor
    private lateinit var context: Context

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        recentSearchesRepository = FakeRecentSearchesRepository()
        networkMonitor = FakeNetworkMonitor(initiallyOnline = true)
        context = mockk {
            every { getString(R.string.search_generic_error) } returns "Something went wrong"
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() =
        SearchViewModel(repository, recentSearchesRepository, networkMonitor, context)

    @Test
    fun `starts in Idle state when online`() {
        val viewModel = createViewModel()

        assertEquals(SearchUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `starts in NoNetwork state when offline`() {
        networkMonitor.setOnline(false)

        val viewModel = createViewModel()

        assertEquals(SearchUiState.NoNetwork, viewModel.uiState.value)
    }

    @Test
    fun `search with blank query does nothing`() = runTest {
        val viewModel = createViewModel()

        viewModel.onQueryChange("   ")
        viewModel.search()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(SearchUiState.Idle, viewModel.uiState.value)
        coVerify(exactly = 0) { repository.searchQuestions(any()) }
    }

    @Test
    fun `search while offline shows NoNetwork without calling repository`() = runTest {
        val viewModel = createViewModel()
        networkMonitor.setOnline(false)

        viewModel.onQueryChange("android")
        viewModel.search()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(SearchUiState.NoNetwork, viewModel.uiState.value)
        coVerify(exactly = 0) { repository.searchQuestions(any()) }
    }

    @Test
    fun `successful search emits Loading then Success and records recent search`() = runTest {
        val questions = listOf(sampleQuestion())
        coEvery { repository.searchQuestions("android") } returns Result.success(questions)

        val viewModel = createViewModel()
        viewModel.onQueryChange("android")

        viewModel.uiState.test {
            assertEquals(SearchUiState.Idle, awaitItem())
            viewModel.search()
            assertEquals(SearchUiState.Loading, awaitItem())
            assertEquals(SearchUiState.Success(questions), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        assertEquals(listOf("android"), recentSearchesRepository.currentValue)
    }

    @Test
    fun `search failure with IOException shows NoNetwork`() = runTest {
        coEvery { repository.searchQuestions("android") } returns Result.failure(IOException("boom"))

        val viewModel = createViewModel()
        viewModel.onQueryChange("android")
        viewModel.search()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(SearchUiState.NoNetwork, viewModel.uiState.value)
    }

    @Test
    fun `search failure with other exception shows Error with its message`() = runTest {
        coEvery { repository.searchQuestions("android") } returns Result.failure(RuntimeException("custom error"))

        val viewModel = createViewModel()
        viewModel.onQueryChange("android")
        viewModel.search()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(SearchUiState.Error("custom error"), viewModel.uiState.value)
    }

    @Test
    fun `search failure with no message falls back to generic string resource`() = runTest {
        coEvery { repository.searchQuestions("android") } returns Result.failure(RuntimeException())

        val viewModel = createViewModel()
        viewModel.onQueryChange("android")
        viewModel.search()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(SearchUiState.Error("Something went wrong"), viewModel.uiState.value)
    }

    @Test
    fun `onQueryChange to blank resets state to Idle`() = runTest {
        coEvery { repository.searchQuestions("android") } returns Result.success(emptyList())
        val viewModel = createViewModel()
        viewModel.onQueryChange("android")
        viewModel.search()
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.uiState.value is SearchUiState.Success)

        viewModel.onQueryChange("")

        assertEquals(SearchUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `onRecentSearchClick sets query and triggers search`() = runTest {
        coEvery { repository.searchQuestions("kotlin") } returns Result.success(emptyList())
        val viewModel = createViewModel()

        viewModel.onRecentSearchClick("kotlin")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("kotlin", viewModel.query.value)
        coVerify { repository.searchQuestions("kotlin") }
    }

    @Test
    fun `onClearRecentSearches delegates to repository`() = runTest {
        recentSearchesRepository.addSearch("android")
        val viewModel = createViewModel()

        viewModel.onClearRecentSearches()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(recentSearchesRepository.clearCalled)
    }

    @Test
    fun `retry with blank query and online resets to Idle`() {
        val viewModel = createViewModel()

        viewModel.retry()

        assertEquals(SearchUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `retry with blank query and offline shows NoNetwork`() {
        val viewModel = createViewModel()
        networkMonitor.setOnline(false)

        viewModel.retry()

        assertEquals(SearchUiState.NoNetwork, viewModel.uiState.value)
    }

    @Test
    fun `retry with a query re-runs search`() = runTest {
        coEvery { repository.searchQuestions("android") } returns Result.success(emptyList())
        val viewModel = createViewModel()
        viewModel.onQueryChange("android")

        viewModel.retry()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.searchQuestions("android") }
    }

    @Test
    fun `dismissNoNetwork resets state to Idle`() {
        networkMonitor.setOnline(false)
        val viewModel = createViewModel()
        assertEquals(SearchUiState.NoNetwork, viewModel.uiState.value)

        viewModel.dismissNoNetwork()

        assertEquals(SearchUiState.Idle, viewModel.uiState.value)
    }

    private fun sampleQuestion() = Question(
        id = 1L,
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
}
