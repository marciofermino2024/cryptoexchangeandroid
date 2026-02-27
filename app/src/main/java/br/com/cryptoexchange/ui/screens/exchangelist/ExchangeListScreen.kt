package br.com.cryptoexchange.ui.screens.exchangelist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.cryptoexchange.BuildConfig
import br.com.cryptoexchange.R
import br.com.cryptoexchange.data.CMCLogger
import br.com.cryptoexchange.domain.model.Exchange
import br.com.cryptoexchange.ui.UiState
import br.com.cryptoexchange.ui.components.*

@Composable
fun ExchangeListScreen(
    onExchangeClick: (Exchange) -> Unit,
    viewModel: ExchangeListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
    var showImageDebug by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_title_exchanges)) },
                actions = {
                    if (BuildConfig.DEBUG) {
                        IconButton(
                            onClick = { showImageDebug = true },
                            modifier = Modifier.semantics { contentDescription = "debug_img_button" }
                        ) {
                            Icon(Icons.Default.BugReport, contentDescription = "Debug Image Logs")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .semantics { contentDescription = "exchange_list_screen" }
        ) {
            when (val s = state) {
                is UiState.Idle -> Unit
                is UiState.Loading -> LoadingView()
                is UiState.Empty -> EmptyStateView()
                is UiState.Error -> ErrorView(
                    error = s.error,
                    onRetry = viewModel::retry
                )
                is UiState.Success -> ExchangeList(
                    exchanges = s.data,
                    isLoadingMore = isLoadingMore,
                    onExchangeClick = onExchangeClick,
                    onLoadMore = viewModel::loadMoreIfNeeded,
                    onRefresh = viewModel::loadInitial
                )
            }
        }
    }

    if (showImageDebug && BuildConfig.DEBUG) {
        ImageDebugSheet(onDismiss = { showImageDebug = false })
    }
}

@Composable
private fun ExchangeList(
    exchanges: List<Exchange>,
    isLoadingMore: Boolean,
    onExchangeClick: (Exchange) -> Unit,
    onLoadMore: (Exchange) -> Unit,
    onRefresh: () -> Unit
) {
    var isRefreshing by remember { mutableStateOf(false) }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            onRefresh()
            isRefreshing = false
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .semantics { contentDescription = "exchange_list" },
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(items = exchanges, key = { it.id }) { exchange ->
                ExchangeRowItem(
                    exchange = exchange,
                    modifier = Modifier.clickable { onExchangeClick(exchange) }
                )
                LaunchedEffect(exchange.id) { onLoadMore(exchange) }
                HorizontalDivider()
            }

            if (isLoadingMore) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                }
            }
        }
    }
}

// ─── Image Debug Sheet (DEBUG only) ─────────────────────────────────────────

@Composable
private fun ImageDebugSheet(onDismiss: () -> Unit) {
    val logs = remember { CMCLogger().imageLogs } // shown from logger singleton via Hilt if available

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.debug_image_logs),
                style = MaterialTheme.typography.titleMedium
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            if (logs.isEmpty()) {
                Text(
                    text = "No image logs yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else {
                logs.forEach { log ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = if (log.cacheHit) "✅" else "🌐",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            log.failureReason?.let {
                                Text("✖ $it", style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.error)
                            }
                            Text(
                                text = log.url.takeLast(60),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                        Text(
                            text = "${log.statusCode ?: "?"} / ${"%.0f".format(log.latencyMs)}ms",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (log.statusCode == 200) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.error
                        )
                    }
                    HorizontalDivider()
                }
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}
