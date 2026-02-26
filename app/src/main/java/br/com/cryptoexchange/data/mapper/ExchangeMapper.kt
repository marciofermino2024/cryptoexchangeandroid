package br.com.cryptoexchange.data.mapper

import br.com.cryptoexchange.data.dto.ExchangeInfoDto
import br.com.cryptoexchange.data.dto.MarketPairDto
import br.com.cryptoexchange.domain.model.Exchange
import br.com.cryptoexchange.domain.model.ExchangeMarketPair
import br.com.cryptoexchange.domain.model.MarketCurrency
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object ExchangeMapper {

    fun map(dto: ExchangeInfoDto): Exchange {
        return Exchange(
            id = dto.id,
            name = dto.name,
            slug = dto.slug,
            logoUrl = dto.logo,
            description = dto.description,
            websiteUrl = dto.urls?.website?.firstOrNull(),
            dateLaunched = parseDate(dto.dateLaunched),
            spotVolumeUsd = dto.spotVolumeUsd,
            makerFee = dto.makerFee,
            takerFee = dto.takerFee,
            weeklyVisits = dto.weeklyVisits,
            spot = dto.spot
        )
    }

    /**
     * Handles ISO8601 dates with fractional seconds ("2017-07-14T00:00:00.000Z")
     * and without ("2017-07-14T00:00:00Z"), matching iOS behaviour exactly.
     */
    fun parseDate(dateString: String?): Date? {
        if (dateString.isNullOrBlank()) return null

        // With fractional seconds (most common in CMC API)
        val withMs = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        runCatching { return withMs.parse(dateString) }

        // Without fractional seconds
        val noMs = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        runCatching { return noMs.parse(dateString) }

        // yyyy-MM-dd prefix fallback
        val simple = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        return runCatching { simple.parse(dateString.take(10)) }.getOrNull()
    }
}

object MarketPairMapper {

    fun map(dto: MarketPairDto, index: Int): ExchangeMarketPair? {
        val base = dto.marketPairBase ?: return null
        val quote = dto.marketPairQuote ?: return null

        val baseCurrencyId = base.currencyId ?: return null
        val baseSymbol = base.currencySymbol ?: return null
        val baseExchangeSymbol = base.exchangeSymbol ?: return null
        val baseCurrencyType = base.currencyType ?: return null

        val quoteCurrencyId = quote.currencyId ?: return null
        val quoteSymbol = quote.currencySymbol ?: return null
        val quoteExchangeSymbol = quote.exchangeSymbol ?: return null
        val quoteCurrencyType = quote.currencyType ?: return null

        val priceUsd = dto.quote?.usd?.price ?: dto.quote?.exchangeReported?.price
        val volumeUsd = dto.quote?.usd?.volume24hUsd
        val id = dto.marketId?.toString() ?: index.toString()

        return ExchangeMarketPair(
            id = id,
            marketPairBase = MarketCurrency(
                currencyId = baseCurrencyId,
                currencySymbol = baseSymbol,
                exchangeSymbol = baseExchangeSymbol,
                currencyType = baseCurrencyType
            ),
            marketPairQuote = MarketCurrency(
                currencyId = quoteCurrencyId,
                currencySymbol = quoteSymbol,
                exchangeSymbol = quoteExchangeSymbol,
                currencyType = quoteCurrencyType
            ),
            priceUsd = priceUsd,
            volumeUsd24h = volumeUsd,
            lastUpdated = null
        )
    }
}
