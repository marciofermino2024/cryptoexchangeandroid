package br.com.cryptoexchange.data.dto

import com.google.gson.annotations.SerializedName

// GET /v1/exchange/map
data class ExchangeMapResponseDto(
    @SerializedName("status") val status: StatusDto,
    @SerializedName("data") val data: List<ExchangeMapItemDto>
)

data class StatusDto(
    @SerializedName("error_code") val errorCode: Int,
    @SerializedName("error_message") val errorMessage: String?
)

data class ExchangeMapItemDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("is_active") val isActive: Int?,
    @SerializedName("first_historical_data") val firstHistoricalData: String?,
    @SerializedName("last_historical_data") val lastHistoricalData: String?
)

// GET /v1/exchange/info
data class ExchangeInfoResponseDto(
    @SerializedName("status") val status: StatusDto,
    @SerializedName("data") val data: Map<String, ExchangeInfoDto>
)

data class ExchangeInfoDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("logo") val logo: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("date_launched") val dateLaunched: String?,
    @SerializedName("notice") val notice: String?,
    @SerializedName("spot_volume_usd") val spotVolumeUsd: Double?,
    @SerializedName("maker_fee") val makerFee: Double?,
    @SerializedName("taker_fee") val takerFee: Double?,
    @SerializedName("weekly_visits") val weeklyVisits: Int?,
    @SerializedName("spot") val spot: Int?,
    @SerializedName("urls") val urls: ExchangeUrlsDto?
)

data class ExchangeUrlsDto(
    @SerializedName("website") val website: List<String>?,
    @SerializedName("blog") val blog: List<String>?,
    @SerializedName("chat") val chat: List<String>?,
    @SerializedName("fee") val fee: List<String>?,
    @SerializedName("twitter") val twitter: List<String>?
)

// GET /v1/exchange/market-pairs/latest
data class ExchangeMarketPairsResponseDto(
    @SerializedName("status") val status: StatusDto,
    @SerializedName("data") val data: ExchangeMarketPairsDataDto
)

data class ExchangeMarketPairsDataDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("num_market_pairs") val numMarketPairs: Int?,
    @SerializedName("market_pairs") val marketPairs: List<MarketPairDto>?
)

data class MarketPairDto(
    @SerializedName("rank_id") val rankId: Int?,
    @SerializedName("market_id") val marketId: Int?,
    @SerializedName("market_pair_base") val marketPairBase: MarketPairCurrencyDto?,
    @SerializedName("market_pair_quote") val marketPairQuote: MarketPairCurrencyDto?,
    @SerializedName("quote") val quote: MarketPairQuoteContainerDto?
)

data class MarketPairCurrencyDto(
    @SerializedName("currency_id") val currencyId: Int?,
    @SerializedName("currency_symbol") val currencySymbol: String?,
    @SerializedName("exchange_symbol") val exchangeSymbol: String?,
    @SerializedName("currency_type") val currencyType: String?
)

data class MarketPairQuoteContainerDto(
    @SerializedName("exchange_reported") val exchangeReported: MarketPairQuoteDto?,
    @SerializedName("USD") val usd: MarketPairQuoteDto?
)

data class MarketPairQuoteDto(
    @SerializedName("price") val price: Double?,
    @SerializedName("volume_24h_base") val volume24hBase: Double?,
    @SerializedName("volume_24h_quote") val volume24hQuote: Double?,
    @SerializedName("volume_24h") val volume24hUsd: Double?,
    @SerializedName("last_updated") val lastUpdated: String?
)
