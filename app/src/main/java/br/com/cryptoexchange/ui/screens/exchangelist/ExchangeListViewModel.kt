package br.com.cryptoexchange.ui.screens.exchangelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.cryptoexchange.data.AppError
import br.com.cryptoexchange.domain.model.Exchange
import br.com.cryptoexchange.domain.usecase.GetExchangeListUseCase
import br.com.cryptoexchange.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExchangeListViewModel @Inject constructor(
    private val getExchangeListUseCase: GetExchangeListUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<List<Exchange>>>(UiState.Idle)
    val state: StateFlow<UiState<List<Exchange>>> = _state.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private val pageSize = 20
    private var currentPage = 1
    private val allExchanges = mutableListOf<Exchange>()
    private var hasMore = true
    private var currentJob: Job? = null

    init {
        loadInitial()
    }

    fun loadInitial() {
        currentJob?.cancel()
        currentPage = 1
        allExchanges.clear()
        hasMore = true
        _state.value = UiState.Loading
        currentJob = viewModelScope.launch { fetchPage(start = 1) }
    }

    fun retry() = loadInitial()

    fun loadMoreIfNeeded(currentItem: Exchange) {
        if (!hasMore || _isLoadingMore.value) return
        val list = (_state.value as? UiState.Success)?.data ?: return
        val thresholdIndex = (list.size - 3).coerceAtLeast(0)
        val currentIndex = list.indexOfFirst { it.id == currentItem.id }
        if (currentIndex >= thresholdIndex) {
            _isLoadingMore.value = true
            currentPage++
            currentJob = viewModelScope.launch {
                fetchPage(start = (currentPage - 1) * pageSize + 1)
            }
        }
    }

    private suspend fun fetchPage(start: Int) {
        try {
            val result = getExchangeListUseCase(start = start, limit = pageSize)
            if (result.isEmpty()) {
                hasMore = false
                if (allExchanges.isEmpty()) _state.value = UiState.Empty
            } else {
                allExchanges.addAll(result)
                hasMore = result.size == pageSize
                _state.value = UiState.Success(allExchanges.toList())
            }
        } catch (e: AppError) {
            if (allExchanges.isEmpty()) _state.value = UiState.Error(e)
        } catch (e: Exception) {
            if (allExchanges.isEmpty()) _state.value = UiState.Error(AppError.Unknown(e.message ?: "Unknown error"))
        } finally {
            _isLoadingMore.value = false
        }
    }
}
