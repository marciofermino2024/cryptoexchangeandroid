package br.com.cryptoexchange.ui.screens.exchangedetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.cryptoexchange.data.AppError
import br.com.cryptoexchange.domain.model.Exchange
import br.com.cryptoexchange.domain.model.ExchangeMarketPair
import br.com.cryptoexchange.domain.usecase.GetExchangeDetailUseCase
import br.com.cryptoexchange.domain.usecase.GetExchangeMarketPairsUseCase
import br.com.cryptoexchange.ui.UiState
import br.com.cryptoexchange.ui.navigation.NavArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExchangeDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getExchangeDetailUseCase: GetExchangeDetailUseCase,
    private val getExchangeMarketPairsUseCase: GetExchangeMarketPairsUseCase
) : ViewModel() {

    private val exchangeId: Int = checkNotNull(savedStateHandle[NavArgs.EXCHANGE_ID])

    private val _detailState = MutableStateFlow<UiState<Exchange>>(UiState.Idle)
    val detailState: StateFlow<UiState<Exchange>> = _detailState.asStateFlow()

    private val _pairsState = MutableStateFlow<UiState<List<ExchangeMarketPair>>>(UiState.Idle)
    val pairsState: StateFlow<UiState<List<ExchangeMarketPair>>> = _pairsState.asStateFlow()

    private val _isLoadingMorePairs = MutableStateFlow(false)
    val isLoadingMorePairs: StateFlow<Boolean> = _isLoadingMorePairs.asStateFlow()

    private val allPairs = mutableListOf<ExchangeMarketPair>()
    private var currentPairsPage = 1
    private val pairsPageSize = 20
    private var hasMorePairs = true

    init {
        load()
    }

    fun load() {
        _detailState.value = UiState.Loading
        _pairsState.value = UiState.Loading
        viewModelScope.launch {
            fetchDetail()
            fetchPairs(start = 1)
        }
    }

    fun retry() = load()

    fun loadMorePairsIfNeeded(currentItem: ExchangeMarketPair) {
        if (!hasMorePairs || _isLoadingMorePairs.value) return
        val list = (_pairsState.value as? UiState.Success)?.data ?: return
        val thresholdIndex = (list.size - 3).coerceAtLeast(0)
        val currentIndex = list.indexOfFirst { it.id == currentItem.id }
        if (currentIndex >= thresholdIndex) {
            _isLoadingMorePairs.value = true
            currentPairsPage++
            viewModelScope.launch {
                fetchPairs(start = (currentPairsPage - 1) * pairsPageSize + 1)
            }
        }
    }

    private suspend fun fetchDetail() {
        try {
            val exchange = getExchangeDetailUseCase(id = exchangeId)
            _detailState.value = UiState.Success(exchange)
        } catch (e: AppError) {
            _detailState.value = UiState.Error(e)
        } catch (e: Exception) {
            _detailState.value = UiState.Error(AppError.Unknown(e.message ?: "Unknown error"))
        }
    }

    private suspend fun fetchPairs(start: Int) {
        try {
            val result = getExchangeMarketPairsUseCase(
                exchangeId = exchangeId, start = start, limit = pairsPageSize
            )
            if (result.isEmpty()) {
                hasMorePairs = false
                if (allPairs.isEmpty()) _pairsState.value = UiState.Empty
            } else {
                allPairs.addAll(result)
                hasMorePairs = result.size == pairsPageSize
                _pairsState.value = UiState.Success(allPairs.toList())
            }
        } catch (e: AppError) {
            if (allPairs.isEmpty()) _pairsState.value = UiState.Error(e)
        } catch (e: Exception) {
            if (allPairs.isEmpty()) _pairsState.value = UiState.Error(AppError.Unknown(e.message ?: "Unknown"))
        } finally {
            _isLoadingMorePairs.value = false
        }
    }
}
