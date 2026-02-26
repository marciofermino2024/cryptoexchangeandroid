package br.com.cryptoexchange.data

sealed class AppError(message: String) : Exception(message) {
    data object MissingApiKey : AppError("CMC_API_KEY is not configured. Please set it in local.properties.")
    data object NetworkOffline : AppError("No internet connection. Check your connection and try again.")
    data object Timeout : AppError("Request timed out. Please try again.")
    data class HttpError(val statusCode: Int) : AppError("Server returned error $statusCode. Please try again later.")
    data object DecodingError : AppError("Failed to parse server response.")
    data class Unknown(val detail: String) : AppError(detail)

    val userFriendlyMessage: String get() = message ?: "Unexpected error"

    val technicalDescription: String get() = when (this) {
        is MissingApiKey -> "BuildConfig.CMC_API_KEY is empty"
        is NetworkOffline -> "IOException: network unreachable"
        is Timeout -> "SocketTimeoutException"
        is HttpError -> "HTTP $statusCode"
        is DecodingError -> "JsonSyntaxException / Gson parsing failed"
        is Unknown -> detail
    }
}
