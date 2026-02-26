package br.com.cryptoexchange.data.mapper

import br.com.cryptoexchange.data.dto.ExchangeInfoDto
import br.com.cryptoexchange.data.dto.ExchangeUrlsDto
import br.com.cryptoexchange.data.dto.MarketPairCurrencyDto
import br.com.cryptoexchange.data.dto.MarketPairDto
import br.com.cryptoexchange.data.dto.MarketPairQuoteContainerDto
import br.com.cryptoexchange.data.dto.MarketPairQuoteDto
import org.junit.Assert.*
import org.junit.Test

class ExchangeMapperTest {

    // ─── ExchangeMapper ──────────────────────────────────────────────────────

    @Test
    fun `map ExchangeInfoDto with all fields produces correct Exchange`() {
        val dto = ExchangeInfoDto(
            id = 270,
            name = "Binance",
            slug = "binance",
            logo = "https://s2.coinmarketcap.com/static/img/exchanges/64x64/270.png",
            description = "World's largest exchange",
            dateLaunched = "2017-07-14T00:00:00.000Z",
            notice = null,
            spotVolumeUsd = 9_500_000_000.0,
            makerFee = 0.001,
            takerFee = 0.001,
            weeklyVisits = 5_000_000,
            spot = 1000,
            urls = ExchangeUrlsDto(
                website = listOf("https://binance.com"),
                blog = null, chat = null, fee = null, twitter = null
            )
        )

        val result = ExchangeMapper.map(dto)

        assertEquals(270, result.id)
        assertEquals("Binance", result.name)
        assertEquals("binance", result.slug)
        assertEquals("https://s2.coinmarketcap.com/static/img/exchanges/64x64/270.png", result.logoUrl)
        assertEquals("World's largest exchange", result.description)
        assertEquals("https://binance.com", result.websiteUrl)
        assertNotNull(result.dateLaunched)
        assertEquals(9_500_000_000.0, result.spotVolumeUsd!!, 0.01)
        assertEquals(0.001, result.makerFee!!, 0.0001)
        assertEquals(0.001, result.takerFee!!, 0.0001)
    }

    @Test
    fun `map ExchangeInfoDto with null optional fields produces null domain fields`() {
        val dto = ExchangeInfoDto(
            id = 1, name = "Test", slug = "test",
            logo = null, description = null, dateLaunched = null, notice = null,
            spotVolumeUsd = null, makerFee = null, takerFee = null,
            weeklyVisits = null, spot = null, urls = null
        )

        val result = ExchangeMapper.map(dto)

        assertNull(result.logoUrl)
        assertNull(result.description)
        assertNull(result.dateLaunched)
        assertNull(result.spotVolumeUsd)
        assertNull(result.makerFee)
        assertNull(result.takerFee)
        assertNull(result.websiteUrl)
    }

    @Test
    fun `parseDate handles ISO8601 with fractional seconds`() {
        val date = ExchangeMapper.parseDate("2017-07-14T00:00:00.000Z")
        assertNotNull("Should parse date with fractional seconds", date)
    }

    @Test
    fun `parseDate handles ISO8601 without fractional seconds`() {
        val date = ExchangeMapper.parseDate("2017-07-14T00:00:00Z")
        assertNotNull("Should parse date without fractional seconds", date)
    }

    @Test
    fun `parseDate falls back to yyyy-MM-dd prefix`() {
        val date = ExchangeMapper.parseDate("2017-07-14")
        assertNotNull("Should parse date from yyyy-MM-dd", date)
    }

    @Test
    fun `parseDate returns null for null input`() {
        assertNull(ExchangeMapper.parseDate(null))
    }

    @Test
    fun `parseDate returns null for blank input`() {
        assertNull(ExchangeMapper.parseDate(""))
    }

    @Test
    fun `parseDate returns null for invalid date string`() {
        assertNull(ExchangeMapper.parseDate("not-a-date"))
    }

    // ─── MarketPairMapper ────────────────────────────────────────────────────

    @Test
    fun `map MarketPairDto with full data produces ExchangeMarketPair`() {
        val dto = MarketPairDto(
            rankId = 1,
            marketId = 9933,
            marketPairBase = MarketPairCurrencyDto(1, "BTC", "BTC", "cryptocurrency"),
            marketPairQuote = MarketPairCurrencyDto(825, "USDT", "USDT", "cryptocurrency"),
            quote = MarketPairQuoteContainerDto(
                exchangeReported = null,
                usd = MarketPairQuoteDto(
                    price = 43_500.0,
                    volume24hBase = 12_000.0,
                    volume24hQuote = 522_000_000.0,
                    volume24hUsd = 522_000_000.0,
                    lastUpdated = null
                )
            )
        )

        val result = MarketPairMapper.map(dto, index = 0)

        assertNotNull(result)
        assertEquals("9933", result!!.id)
        assertEquals("BTC", result.marketPairBase.currencySymbol)
        assertEquals("USDT", result.marketPairQuote.currencySymbol)
        assertEquals(43_500.0, result.priceUsd!!, 0.01)
        assertEquals(522_000_000.0, result.volumeUsd24h!!, 0.01)
    }

    @Test
    fun `map MarketPairDto with missing base returns null`() {
        val dto = MarketPairDto(rankId = 1, marketId = 1, marketPairBase = null, marketPairQuote = null, quote = null)
        assertNull(MarketPairMapper.map(dto, index = 0))
    }

    @Test
    fun `map MarketPairDto uses index as id when marketId is null`() {
        val dto = MarketPairDto(
            rankId = 1,
            marketId = null,
            marketPairBase = MarketPairCurrencyDto(1, "BTC", "BTC", "crypto"),
            marketPairQuote = MarketPairCurrencyDto(825, "USDT", "USDT", "crypto"),
            quote = null
        )
        val result = MarketPairMapper.map(dto, index = 7)
        assertEquals("7", result?.id)
    }
}
