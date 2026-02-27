package br.com.cryptoexchange.data

import android.util.Log
import br.com.cryptoexchange.BuildConfig
import java.util.Date
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.concurrent.withLock

// ─── Image Log Entry ────────────────────────────────────────────────────────

data class ImageLogEntry(
    val url: String,
    val statusCode: Int?,
    val latencyMs: Double,
    val bytes: Int?,
    val mimeType: String?,
    val cacheHit: Boolean,
    val failureReason: String?,
    val timestamp: Date = Date()
) {
    val summary: String get() {
        val status = statusCode?.toString() ?: "—"
        val cache = if (cacheHit) "✅HIT" else "❌MISS"
        val ms = "%.0f".format(latencyMs)
        val size = bytes?.let { "${it}B" } ?: "—"
        return "[$cache] $status ${ms}ms $size $url"
    }
}

// ─── Request Log ────────────────────────────────────────────────────────────

data class RequestLog(
    val requestId: String,
    val url: String,
    val statusCode: Int?,
    val latencyMs: Double?,
    val bodySizeBytes: Int?,
    val error: String?,
    val jsonSnippet: String?,
    val decodingPath: String?
)

// ─── CMCLogger ──────────────────────────────────────────────────────────────

@Singleton
class CMCLogger @Inject constructor() {

    companion object {
        private const val TAG_API = "CMC"
        private const val TAG_IMG = "IMG"
        private const val MAX_IMAGE_LOGS = 20
        private const val JSON_SNIPPET_LENGTH = 1500
    }

    @Volatile
    var lastErrorContext: RequestLog? = null
        private set

    private val imageLogsLock = ReentrantLock()
    private val _imageLogs = ArrayDeque<ImageLogEntry>()

    val imageLogs: List<ImageLogEntry>
        get() = imageLogsLock.withLock { _imageLogs.toList() }

    // ─── API Logging ────────────────────────────────────────────────────────

    fun logRequest(id: String, url: String, apiKey: String) {
        val masked = maskApiKey(apiKey)
        logDebug(TAG_API, "▶ id=$id url=$url key=***$masked")
    }

    fun logResponse(id: String, statusCode: Int, latencyMs: Double, bodySize: Int) {
        if (statusCode >= 400) {
            logError(TAG_API, "◀ id=$id status=$statusCode ${"%.0f".format(latencyMs)}ms ${bodySize}B")
        } else {
            logDebug(TAG_API, "◀ id=$id status=$statusCode ${"%.0f".format(latencyMs)}ms ${bodySize}B")
        }
    }

    fun logDecodingError(id: String, url: String, error: Throwable, rawBody: String?) {
        val snippet = rawBody?.take(JSON_SNIPPET_LENGTH) ?: "<no body>"
        logError(TAG_API, "✖ DECODE id=$id error=${error.message}")
        logDebug(TAG_API, "✖ JSON: $snippet")
        lastErrorContext = RequestLog(
            requestId = id,
            url = url,
            statusCode = null,
            latencyMs = null,
            bodySizeBytes = rawBody?.length,
            error = error.message,
            jsonSnippet = snippet,
            decodingPath = error.message
        )
    }

    fun logNetworkError(id: String, url: String, error: Throwable, statusCode: Int? = null) {
        logError(TAG_API, "✖ NET id=$id status=${statusCode ?: -1} err=${error.message}")
        lastErrorContext = RequestLog(
            requestId = id,
            url = url,
            statusCode = statusCode,
            latencyMs = null,
            bodySizeBytes = null,
            error = error.message,
            jsonSnippet = null,
            decodingPath = null
        )
    }

    // ─── Image Logging ───────────────────────────────────────────────────────

    fun logLogoUrl(exchangeId: Int, name: String, logoUrl: String?) {
        if (!logoUrl.isNullOrBlank()) {
            logDebug(TAG_IMG, "exchange id=$exchangeId name=$name logoURL=$logoUrl")
        } else {
            logWarn(TAG_IMG, "⚠️ MISSING LOGO id=$exchangeId name=$name")
        }
    }

    fun logImageResult(
        url: String,
        statusCode: Int?,
        latencyMs: Double,
        bytes: Int?,
        mimeType: String?,
        cacheHit: Boolean,
        failureReason: String?
    ) {
        val entry = ImageLogEntry(
            url = url,
            statusCode = statusCode,
            latencyMs = latencyMs,
            bytes = bytes,
            mimeType = mimeType,
            cacheHit = cacheHit,
            failureReason = failureReason
        )
        imageLogsLock.withLock {
            _imageLogs.addFirst(entry)
            while (_imageLogs.size > MAX_IMAGE_LOGS) _imageLogs.removeLast()
        }
        if (failureReason != null) {
            logError(TAG_IMG, "✖ $url reason=$failureReason")
        } else {
            logDebug(TAG_IMG, "✔ ${entry.summary}")
        }
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    fun maskApiKey(key: String): String =
        if (key.length >= 4) key.takeLast(4) else "****"

    private fun logDebug(tag: String, msg: String) {
        if (BuildConfig.DEBUG) Log.d(tag, msg)
    }

    private fun logError(tag: String, msg: String) {
        Log.e(tag, msg)
    }

    private fun logWarn(tag: String, msg: String) {
        Log.w(tag, msg)
    }
}
