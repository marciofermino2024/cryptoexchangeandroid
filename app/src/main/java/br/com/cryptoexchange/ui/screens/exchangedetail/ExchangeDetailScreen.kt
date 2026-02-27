package br.com.cryptoexchange.ui.screens.exchangedetail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.cryptoexchange.R
import br.com.cryptoexchange.domain.model.Exchange
import br.com.cryptoexchange.domain.model.ExchangeMarketPair
import br.com.cryptoexchange.ui.UiState
import br.com.cryptoexchange.ui.components.*
import java.text.DateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExchangeDetailScreen(
    onNavigateUp: () -> Unit,
    viewModel: ExchangeDetailViewModel = hiltViewModel()
) {
    val detailState by viewModel.detailState.collectAsState()
    val pairsState by viewModel.pairsState.collectAsState()
    val isLoadingMorePairs by viewModel.isLoadingMorePairs.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val title = (detailState as? UiState.Success)?.data?.name ?: ""
                    Text(title)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        modifier = Modifier.semantics { contentDescription = "exchange_detail_screen" }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val s = detailState) {
                is UiState.Idle -> Unit
                is UiState.Loading -> LoadingView()
                is UiState.Empty -> EmptyStateView()
                is UiState.Error -> ErrorView(error = s.error, onRetry = viewModel::retry)
                is UiState.Success -> DetailContent(
                    exchange = s.data,
                    pairsState = pairsState,
                    isLoadingMorePairs = isLoadingMorePairs,
                    onLoadMorePairs = viewModel::loadMorePairsIfNeeded,
                    onRetryPairs = viewModel::retry
                )
            }
        }
    }
}

@Composable
private fun DetailContent(
    exchange: Exchange,
    pairsState: UiState<List<ExchangeMarketPair>>,
    isLoadingMorePairs: Boolean,
    onLoadMorePairs: (ExchangeMarketPair) -> Unit,
    onRetryPairs: () -> Unit
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // ── Header ──────────────────────────────────────────────────────────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ExchangeLogo(logoUrl = exchange.logoUrl, sizeDp = 64)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(exchange.name, style = MaterialTheme.typography.headlineSmall)
                    Text(
                        text = stringResource(R.string.detail_id, exchange.id),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
            HorizontalDivider()
        }

        // ── Description ─────────────────────────────────────────────────────
        if (!exchange.description.isNullOrBlank()) {
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(stringResource(R.string.detail_description), style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = exchange.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                HorizontalDivider()
            }
        }

        // ── Info Grid ────────────────────────────────────────────────────────
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            ) {
                Column {
                    val dateFmt = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
                    InfoRow(
                        title = stringResource(R.string.detail_date_launched),
                        value = exchange.dateLaunched?.let { dateFmt.format(it) }
                            ?: stringResource(R.string.not_available)
                    )
                    HorizontalDivider(modifier = Modifier.padding(start = 16.dp))
                    InfoRow(
                        title = stringResource(R.string.detail_volume_usd),
                        value = exchange.spotVolumeUsd?.let { "$%.2f".format(it) }
                            ?: stringResource(R.string.not_available)
                    )
                    HorizontalDivider(modifier = Modifier.padding(start = 16.dp))
                    InfoRow(
                        title = stringResource(R.string.detail_maker_fee),
                        value = exchange.makerFee?.let { "%.4f%%".format(it) }
                            ?: stringResource(R.string.not_available)
                    )
                    HorizontalDivider(modifier = Modifier.padding(start = 16.dp))
                    InfoRow(
                        title = stringResource(R.string.detail_taker_fee),
                        value = exchange.takerFee?.let { "%.4f%%".format(it) }
                            ?: stringResource(R.string.not_available)
                    )
                }
            }
        }

        // ── Website Link ─────────────────────────────────────────────────────
        if (!exchange.websiteUrl.isNullOrBlank()) {
            item {
                OutlinedButton(
                    onClick = {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(exchange.websiteUrl)))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.OpenInBrowser, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.detail_visit_website))
                }
            }
        }

        item { HorizontalDivider() }

        // ── Market Pairs Header ──────────────────────────────────────────────
        item {
            Text(
                text = stringResource(R.string.detail_market_pairs),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // ── Market Pairs Content ─────────────────────────────────────────────
        when (val ps = pairsState) {
            is UiState.Idle, is UiState.Loading -> item {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is UiState.Empty -> item {
                Text(
                    text = stringResource(R.string.pairs_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            is UiState.Error -> item {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = ps.error.userFriendlyMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                    TextButton(onClick = onRetryPairs) {
                        Text(stringResource(R.string.retry_button))
                    }
                }
            }
            is UiState.Success -> {
                items(items = ps.data, key = { it.id }) { pair ->
                    MarketPairRowItem(
                        pair = pair,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    LaunchedEffect(pair.id) { onLoadMorePairs(pair) }
                    HorizontalDivider(modifier = Modifier.padding(start = 16.dp))
                }
                if (isLoadingMorePairs) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
