package br.com.cryptoexchange.ui

import br.com.cryptoexchange.data.AppError

/**
 * Mirrors iOS UiState<T> enum exactly:
 *   case idle / loading / success(T) / empty / error(AppError)
 */
sealed class UiState<out T> {
    data object Idle : UiState<Nothing>()
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data object Empty : UiState<Nothing>()
    data class Error(val error: AppError) : UiState<Nothing>()
}
