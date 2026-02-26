package br.com.cryptoexchange.domain.repository

import br.com.cryptoexchange.domain.model.Exchange
import br.com.cryptoexchange.domain.model.ExchangeMarketPair

interface ExchangeRepository {
    suspend fun fetchExchangeList(start: Int, limit: Int): List<Exchange>
    suspend fun fetchExchangeDetail(id: Int): Exchange
    suspend fun fetchExchangeMarketPairs(exchangeId: Int, start: Int, limit: Int): List<ExchangeMarketPair>
}
