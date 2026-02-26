package br.com.cryptoexchange.domain.model

import java.util.Date

data class ExchangeMarketPair(
    val id: String,
    val marketPairBase: MarketCurrency,
    val marketPairQuote: MarketCurrency,
    val priceUsd: Double?,
    val volumeUsd24h: Double?,
    val lastUpdated: Date?
)

data class MarketCurrency(
    val currencyId: Int,
    val currencySymbol: String,
    val exchangeSymbol: String,
    val currencyType: String
)
