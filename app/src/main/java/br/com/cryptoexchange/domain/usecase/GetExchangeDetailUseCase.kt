package br.com.cryptoexchange.domain.usecase

import br.com.cryptoexchange.domain.model.Exchange
import br.com.cryptoexchange.domain.repository.ExchangeRepository
import javax.inject.Inject

class GetExchangeDetailUseCase @Inject constructor(
    private val repository: ExchangeRepository
) {
    suspend operator fun invoke(id: Int): Exchange =
        repository.fetchExchangeDetail(id = id)
}
