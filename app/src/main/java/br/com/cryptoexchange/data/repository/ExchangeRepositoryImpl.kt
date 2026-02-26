package br.com.cryptoexchange.data.repository

import br.com.cryptoexchange.data.AppError
import br.com.cryptoexchange.data.CMCLogger
import br.com.cryptoexchange.data.api.CmcApiService
import br.com.cryptoexchange.data.mapper.ExchangeMapper
import br.com.cryptoexchange.data.mapper.MarketPairMapper
import br.com.cryptoexchange.domain.model.Exchange
import br.com.cryptoexchange.domain.model.ExchangeMarketPair
import br.com.cryptoexchange.domain.repository.ExchangeRepository
import com.google.gson.JsonSyntaxException
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExchangeRepositoryImpl @Inject constructor(
    private val api: CmcApiService,
    private val logger: CMCLogger
) : ExchangeRepository {

    // Simple in-memory page cache: "list_start_limit" -> list of exchanges
    private val listCache = ConcurrentHashMap<String, List<Exchange>>()
    private val detailCache = ConcurrentHashMap<Int, Exchange>()

    override suspend fun fetchExchangeList(start: Int, limit: Int): List<Exchange> {
        val cacheKey = "list_${start}_$limit"
        listCache[cacheKey]?.let { return it }

        // Step 1: Get ordered IDs from /exchange/map
        val mapResponse = safeApiCall(
            call = { api.getExchangeMap(start = start, limit = limit) },
            url = "v1/exchange/map?start=$start&limit=$limit"
        )
        val ids = mapResponse.data.map { it.id }
        if (ids.isEmpty()) return emptyList()

        // Step 2: Batch-fetch info (logo, fees, volume) for those IDs
        val idsParam = ids.joinToString(",")
        val infoResponse = safeApiCall(
            call = { api.getExchangeInfo(ids = idsParam) },
            url = "v1/exchange/info?id=$idsParam"
        )

        // Preserve the volume-sorted order from /map
        val infoById = infoResponse.data
        val idOrder = ids.withIndex().associate { (index, id) -> id to index }
        val exchanges = infoById.values
            .map { dto ->
                logger.logLogoUrl(exchangeId = dto.id, name = dto.name, logoUrl = dto.logo)
                ExchangeMapper.map(dto)
            }
            .sortedBy { idOrder[it.id] ?: Int.MAX_VALUE }

        listCache[cacheKey] = exchanges
        return exchanges
    }

    override suspend fun fetchExchangeDetail(id: Int): Exchange {
        detailCache[id]?.let { return it }

        val response = safeApiCall(
            call = { api.getExchangeInfo(ids = id.toString()) },
            url = "v1/exchange/info?id=$id"
        )
        val dto = response.data.values.firstOrNull()
            ?: throw AppError.Unknown("Exchange $id not found in response")

        logger.logLogoUrl(exchangeId = dto.id, name = dto.name, logoUrl = dto.logo)
        val exchange = ExchangeMapper.map(dto)
        detailCache[id] = exchange
        return exchange
    }

    override suspend fun fetchExchangeMarketPairs(
        exchangeId: Int,
        start: Int,
        limit: Int
    ): List<ExchangeMarketPair> {
        val response = safeApiCall(
            call = { api.getExchangeMarketPairs(id = exchangeId, start = start, limit = limit) },
            url = "v1/exchange/market-pairs/latest?id=$exchangeId"
        )
        return (response.data.marketPairs ?: emptyList())
            .mapIndexedNotNull { index, dto -> MarketPairMapper.map(dto, index) }
    }

    // ─── Safe API call wrapper ───────────────────────────────────────────────

    private suspend fun <T> safeApiCall(
        call: suspend () -> retrofit2.Response<T>,
        url: String
    ): T {
        val requestId = java.util.UUID.randomUUID().toString().take(8)
        logger.logRequest(id = requestId, url = url, apiKey = "***")

        val startMs = System.currentTimeMillis()
        return try {
            val response = call()
            val latencyMs = (System.currentTimeMillis() - startMs).toDouble()
            val bodyString = response.errorBody()?.string()

            logger.logResponse(
                id = requestId,
                statusCode = response.code(),
                latencyMs = latencyMs,
                bodySize = response.body().toString().length
            )

            if (!response.isSuccessful) {
                logger.logNetworkError(
                    id = requestId, url = url,
                    error = Exception("HTTP ${response.code()}"),
                    statusCode = response.code()
                )
                throw AppError.HttpError(response.code())
            }

            response.body() ?: run {
                logger.logDecodingError(requestId, url, Exception("Empty body"), null)
                throw AppError.DecodingError
            }
        } catch (e: AppError) {
            throw e
        } catch (e: SocketTimeoutException) {
            logger.logNetworkError(requestId, url, e)
            throw AppError.Timeout
        } catch (e: IOException) {
            logger.logNetworkError(requestId, url, e)
            throw AppError.NetworkOffline
        } catch (e: JsonSyntaxException) {
            logger.logDecodingError(requestId, url, e, null)
            throw AppError.DecodingError
        } catch (e: Exception) {
            logger.logNetworkError(requestId, url, e)
            throw AppError.Unknown(e.message ?: "Unknown error")
        }
    }
}
