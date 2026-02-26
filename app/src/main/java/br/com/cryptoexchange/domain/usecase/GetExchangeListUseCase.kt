package br.com.cryptoexchange.domain.usecase

import br.com.cryptoexchange.domain.model.Exchange
import br.com.cryptoexchange.domain.repository.ExchangeRepository
import javax.inject.Inject

class GetExchangeListUseCase @Inject constructor(
    private val repository: ExchangeRepository
) {
    suspend operator fun invoke(start: Int, limit: Int): List<Exchange> =
        repository.fetchExchangeList(start = start, limit = limit)
}
