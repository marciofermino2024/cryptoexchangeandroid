package br.com.cryptoexchange.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.com.cryptoexchange.BuildConfig
import br.com.cryptoexchange.R
import br.com.cryptoexchange.data.AppError
import br.com.cryptoexchange.data.CMCLogger
import androidx.compose.ui.tooling.preview.Preview
import br.com.cryptoexchange.ui.theme.CryptoExchangeTheme

// ─── LoadingView ─────────────────────────────────────────────────────────────

@Composable
fun LoadingView(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .semantics { contentDescription = "loading_view" },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            CircularProgressIndicator()
            Text(
                text = stringResource(R.string.loading_message),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

// ─── EmptyStateView ──────────────────────────────────────────────────────────

@Composable
fun EmptyStateView(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .semantics { contentDescription = "empty_view" },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Inbox,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
            Text(
                text = stringResource(R.string.empty_state_title),
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = stringResource(R.string.empty_state_message),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}

// ─── ErrorView ───────────────────────────────────────────────────────────────

@Composable
fun ErrorView(
    error: AppError,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    logger: CMCLogger? = null
) {
    var showDebugSheet by remember { mutableStateOf(false) }
    val lastLog = remember { logger?.lastErrorContext }

    Box(
        modifier = modifier
            .fillMaxSize()
            .semantics { contentDescription = "error_view" },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                text = stringResource(R.string.error_title),
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = error.userFriendlyMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onRetry,
                modifier = Modifier
                    .defaultMinSize(minWidth = 160.dp)
                    .semantics { contentDescription = "retry_button" }
            ) {
                Text(
                    text = stringResource(R.string.retry_button),
                    style = MaterialTheme.typography.labelLarge
                )
            }

            // Debug details button — only in DEBUG builds when there's error context
            if (BuildConfig.DEBUG && lastLog != null) {
                TextButton(
                    onClick = { showDebugSheet = true },
                    modifier = Modifier.semantics { contentDescription = "debug_details_button" }
                ) {
                    Icon(
                        imageVector = Icons.Default.BugReport,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.error_debug_details),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }

    if (showDebugSheet && lastLog != null) {
        DebugApiSheet(
            requestId = lastLog.requestId,
            url = lastLog.url,
            statusCode = lastLog.statusCode,
            technicalError = lastLog.error ?: error.technicalDescription,
            jsonSnippet = lastLog.jsonSnippet,
            onDismiss = { showDebugSheet = false }
        )
    }
}

// ─── DebugApiSheet ───────────────────────────────────────────────────────────

@Composable
fun DebugApiSheet(
    requestId: String,
    url: String,
    statusCode: Int?,
    technicalError: String,
    jsonSnippet: String?,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("🐛 API Debug", style = MaterialTheme.typography.titleMedium)
            HorizontalDivider()
            DebugRow("Request ID", requestId)
            DebugRow("URL", url)
            DebugRow("Status Code", statusCode?.toString() ?: "—")
            DebugRow("Error", technicalError)
            if (jsonSnippet != null) {
                Text("JSON Snippet", style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp)
                ) {
                    Text(
                        text = jsonSnippet,
                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
                    )
                }
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun DebugRow(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
        )
    }
}

// ─── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "LoadingView")
@Composable
private fun LoadingViewPreview() {
    CryptoExchangeTheme { LoadingView() }
}

@Preview(showBackground = true, name = "EmptyStateView")
@Composable
private fun EmptyStateViewPreview() {
    CryptoExchangeTheme { EmptyStateView() }
}

@Preview(showBackground = true, name = "ErrorView – NetworkOffline")
@Composable
private fun ErrorViewPreview() {
    CryptoExchangeTheme {
        ErrorView(
            error = AppError.NetworkOffline,
            onRetry = {}
        )
    }
}

@Preview(showBackground = true, name = "DebugApiSheet")
@Composable
private fun DebugApiSheetPreview() {
    CryptoExchangeTheme {
        DebugApiSheet(
            requestId = "abc-123",
            url = "https://pro-api.coinmarketcap.com/v1/exchange/listings/latest",
            statusCode = 401,
            technicalError = "HTTP 401 Unauthorized",
            jsonSnippet = "{\"status\":{\"error_code\":1001}}",
            onDismiss = {}
        )
    }
}
