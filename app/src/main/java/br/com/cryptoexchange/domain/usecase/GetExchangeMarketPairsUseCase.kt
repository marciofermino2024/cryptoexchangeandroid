package br.com.cryptoexchange.domain.usecase

import br.com.cryptoexchange.domain.model.ExchangeMarketPair
import br.com.cryptoexchange.domain.repository.ExchangeRepository
import javax.inject.Inject

class GetExchangeMarketPairsUseCase @Inject constructor(
    private val repository: ExchangeRepository
) {
    suspend operator fun invoke(exchangeId: Int, start: Int, limit: Int): List<ExchangeMarketPair> =
        repository.fetchExchangeMarketPairs(exchangeId = exchangeId, start = start, limit = limit)
}
