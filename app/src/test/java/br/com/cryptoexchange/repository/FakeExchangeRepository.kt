package br.com.cryptoexchange.repository

import br.com.cryptoexchange.data.AppError
import br.com.cryptoexchange.domain.model.Exchange
import br.com.cryptoexchange.domain.model.ExchangeMarketPair
import br.com.cryptoexchange.domain.repository.ExchangeRepository
import java.util.Date

class FakeExchangeRepository : ExchangeRepository {

    enum class Mode { SUCCESS, EMPTY, ERROR_NETWORK, ERROR_HTTP_401 }

    var mode: Mode = Mode.SUCCESS

    val sampleExchanges = (1..5).map { i ->
        Exchange(
            id = i,
            name = "Exchange $i",
            slug = "exchange-$i",
            logoUrl = "https://example.com/logo$i.png",
            description = "Description $i",
            websiteUrl = "https://exchange$i.com",
            dateLaunched = Date(1_600_000_000_000L + i * 86_400_000L),
            spotVolumeUsd = 1_000_000_000.0 * i,
            makerFee = 0.001 * i,
            takerFee = 0.001 * i,
            weeklyVisits = 10_000 * i,
            spot = i
        )
    }

    val samplePairs = (1..5).map { i ->
        ExchangeMarketPair(
            id = "pair-$i",
            marketPairBase = br.com.cryptoexchange.domain.model.MarketCurrency(
                currencyId = i, currencySymbol = "BTC", exchangeSymbol = "BTC", currencyType = "crypto"
            ),
            marketPairQuote = br.com.cryptoexchange.domain.model.MarketCurrency(
                currencyId = 825, currencySymbol = "USDT", exchangeSymbol = "USDT", currencyType = "crypto"
            ),
            priceUsd = 50_000.0 * i,
            volumeUsd24h = 1_000_000.0 * i,
            lastUpdated = null
        )
    }

    override suspend fun fetchExchangeList(start: Int, limit: Int): List<Exchange> = when (mode) {
        Mode.SUCCESS -> sampleExchanges.drop(start - 1).take(limit)
        Mode.EMPTY -> emptyList()
        Mode.ERROR_NETWORK -> throw AppError.NetworkOffline
        Mode.ERROR_HTTP_401 -> throw AppError.HttpError(401)
    }

    override suspend fun fetchExchangeDetail(id: Int): Exchange = when (mode) {
        Mode.SUCCESS -> sampleExchanges.firstOrNull { it.id == id }
            ?: throw AppError.Unknown("Not found: $id")
        Mode.EMPTY -> throw AppError.Unknown("Not found")
        Mode.ERROR_NETWORK -> throw AppError.NetworkOffline
        Mode.ERROR_HTTP_401 -> throw AppError.HttpError(401)
    }

    override suspend fun fetchExchangeMarketPairs(
        exchangeId: Int, start: Int, limit: Int
    ): List<ExchangeMarketPair> = when (mode) {
        Mode.SUCCESS -> samplePairs.drop(start - 1).take(limit)
        Mode.EMPTY -> emptyList()
        Mode.ERROR_NETWORK -> throw AppError.NetworkOffline
        Mode.ERROR_HTTP_401 -> throw AppError.HttpError(401)
    }
}
