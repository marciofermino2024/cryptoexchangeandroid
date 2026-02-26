package br.com.cryptoexchange.domain.usecase

import br.com.cryptoexchange.data.AppError
import br.com.cryptoexchange.repository.FakeExchangeRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GetExchangeListUseCaseTest {

    private lateinit var repository: FakeExchangeRepository
    private lateinit var useCase: GetExchangeListUseCase

    @Before
    fun setUp() {
        repository = FakeExchangeRepository()
        useCase = GetExchangeListUseCase(repository)
    }

    @Test
    fun `invoke returns list on success`() = runTest {
        repository.mode = FakeExchangeRepository.Mode.SUCCESS
        val result = useCase(start = 1, limit = 5)
        assertEquals(5, result.size)
        assertEquals("Exchange 1", result[0].name)
    }

    @Test
    fun `invoke returns empty list when empty`() = runTest {
        repository.mode = FakeExchangeRepository.Mode.EMPTY
        val result = useCase(start = 1, limit = 5)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `invoke throws NetworkOffline when offline`() = runTest {
        repository.mode = FakeExchangeRepository.Mode.ERROR_NETWORK
        try {
            useCase(start = 1, limit = 5)
            fail("Expected AppError.NetworkOffline")
        } catch (e: AppError.NetworkOffline) {
            // pass
        }
    }

    @Test
    fun `invoke throws HttpError 401 when unauthorized`() = runTest {
        repository.mode = FakeExchangeRepository.Mode.ERROR_HTTP_401
        try {
            useCase(start = 1, limit = 5)
            fail("Expected AppError.HttpError")
        } catch (e: AppError.HttpError) {
            assertEquals(401, e.statusCode)
        }
    }
}

class GetExchangeDetailUseCaseTest {

    private lateinit var repository: FakeExchangeRepository
    private lateinit var useCase: GetExchangeDetailUseCase

    @Before
    fun setUp() {
        repository = FakeExchangeRepository()
        useCase = GetExchangeDetailUseCase(repository)
    }

    @Test
    fun `invoke returns exchange for valid id`() = runTest {
        repository.mode = FakeExchangeRepository.Mode.SUCCESS
        val result = useCase(id = 1)
        assertEquals(1, result.id)
        assertEquals("Exchange 1", result.name)
    }

    @Test
    fun `invoke throws Unknown for invalid id`() = runTest {
        repository.mode = FakeExchangeRepository.Mode.SUCCESS
        try {
            useCase(id = 999)
            fail("Expected AppError.Unknown")
        } catch (e: AppError.Unknown) {
            // pass
        }
    }

    @Test
    fun `invoke throws NetworkOffline when offline`() = runTest {
        repository.mode = FakeExchangeRepository.Mode.ERROR_NETWORK
        try {
            useCase(id = 1)
            fail("Expected AppError.NetworkOffline")
        } catch (e: AppError.NetworkOffline) { }
    }
}

class GetExchangeMarketPairsUseCaseTest {

    private lateinit var repository: FakeExchangeRepository
    private lateinit var useCase: GetExchangeMarketPairsUseCase

    @Before
    fun setUp() {
        repository = FakeExchangeRepository()
        useCase = GetExchangeMarketPairsUseCase(repository)
    }

    @Test
    fun `invoke returns pairs on success`() = runTest {
        repository.mode = FakeExchangeRepository.Mode.SUCCESS
        val result = useCase(exchangeId = 1, start = 1, limit = 5)
        assertEquals(5, result.size)
    }

    @Test
    fun `invoke returns empty list when empty`() = runTest {
        repository.mode = FakeExchangeRepository.Mode.EMPTY
        val result = useCase(exchangeId = 1, start = 1, limit = 5)
        assertTrue(result.isEmpty())
    }
}
