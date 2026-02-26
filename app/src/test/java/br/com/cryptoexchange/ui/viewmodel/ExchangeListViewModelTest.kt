package br.com.cryptoexchange.ui.viewmodel

import app.cash.turbine.test
import br.com.cryptoexchange.data.AppError
import br.com.cryptoexchange.domain.usecase.GetExchangeListUseCase
import br.com.cryptoexchange.repository.FakeExchangeRepository
import br.com.cryptoexchange.ui.UiState
import br.com.cryptoexchange.ui.screens.exchangelist.ExchangeListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExchangeListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeExchangeRepository
    private lateinit var viewModel: ExchangeListViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeExchangeRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel(): ExchangeListViewModel =
        ExchangeListViewModel(GetExchangeListUseCase(repository))

    @Test
    fun `initial state is Loading then transitions to Success`() = runTest {
        repository.mode = FakeExchangeRepository.Mode.SUCCESS
        viewModel = buildViewModel()

        viewModel.state.test {
            // First emission from init is Loading
            val first = awaitItem()
            assertTrue("Expected Loading, got $first", first is UiState.Loading)

            // After coroutine runs
            testDispatcher.scheduler.advanceUntilIdle()
            val second = awaitItem()
            assertTrue("Expected Success, got $second", second is UiState.Success)
            val exchanges = (second as UiState.Success).data
            assertTrue(exchanges.isNotEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `empty result transitions to Empty state`() = runTest {
        repository.mode = FakeExchangeRepository.Mode.EMPTY
        viewModel = buildViewModel()

        viewModel.state.test {
            awaitItem() // Loading
            testDispatcher.scheduler.advanceUntilIdle()
            val state = awaitItem()
            assertTrue("Expected Empty, got $state", state is UiState.Empty)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `network error transitions to Error state`() = runTest {
        repository.mode = FakeExchangeRepository.Mode.ERROR_NETWORK
        viewModel = buildViewModel()

        viewModel.state.test {
            awaitItem() // Loading
            testDispatcher.scheduler.advanceUntilIdle()
            val state = awaitItem()
            assertTrue("Expected Error, got $state", state is UiState.Error)
            val error = (state as UiState.Error).error
            assertTrue(error is AppError.NetworkOffline)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `HTTP 401 transitions to Error state with HttpError`() = runTest {
        repository.mode = FakeExchangeRepository.Mode.ERROR_HTTP_401
        viewModel = buildViewModel()

        viewModel.state.test {
            awaitItem() // Loading
            testDispatcher.scheduler.advanceUntilIdle()
            val state = awaitItem()
            assertTrue(state is UiState.Error)
            val error = (state as UiState.Error).error
            assertTrue(error is AppError.HttpError)
            assertEquals(401, (error as AppError.HttpError).statusCode)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `retry resets to Loading then Success`() = runTest {
        repository.mode = FakeExchangeRepository.Mode.ERROR_NETWORK
        viewModel = buildViewModel()

        // Reach error state
        testDispatcher.scheduler.advanceUntilIdle()

        // Switch to success mode and retry
        repository.mode = FakeExchangeRepository.Mode.SUCCESS
        viewModel.state.test {
            cancelAndIgnoreRemainingEvents()
        }

        viewModel.retry()
        viewModel.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            val last = awaitItem()
            // After retry, expect Loading or Success
            assertTrue(last is UiState.Loading || last is UiState.Success)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `isLoadingMore is false initially`() = runTest {
        repository.mode = FakeExchangeRepository.Mode.SUCCESS
        viewModel = buildViewModel()
        assertFalse(viewModel.isLoadingMore.value)
    }
}
