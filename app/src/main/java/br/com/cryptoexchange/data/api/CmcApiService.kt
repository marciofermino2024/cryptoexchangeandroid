package br.com.cryptoexchange.data.api

import br.com.cryptoexchange.data.dto.ExchangeInfoResponseDto
import br.com.cryptoexchange.data.dto.ExchangeMapResponseDto
import br.com.cryptoexchange.data.dto.ExchangeMarketPairsResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CmcApiService {

    /**
     * GET /v1/exchange/map
     * Returns an ordered list of exchanges by volume. Used to get IDs for a page.
     */
    @GET("v1/exchange/map")
    suspend fun getExchangeMap(
        @Query("start") start: Int,
        @Query("limit") limit: Int,
        @Query("sort") sort: String = "volume_24h"
    ): Response<ExchangeMapResponseDto>

    /**
     * GET /v1/exchange/info
     * Returns metadata (logo, description, fees, volume) for comma-separated IDs.
     * CoinMarketCap supports up to ~100 IDs in a single call.
     */
    @GET("v1/exchange/info")
    suspend fun getExchangeInfo(
        @Query("id") ids: String
    ): Response<ExchangeInfoResponseDto>

    /**
     * GET /v1/exchange/market-pairs/latest
     * Returns the latest market pairs for a single exchange ID.
     */
    @GET("v1/exchange/market-pairs/latest")
    suspend fun getExchangeMarketPairs(
        @Query("id") id: Int,
        @Query("start") start: Int,
        @Query("limit") limit: Int
    ): Response<ExchangeMarketPairsResponseDto>
}
