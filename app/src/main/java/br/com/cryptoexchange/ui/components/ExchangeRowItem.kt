package br.com.cryptoexchange.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.com.cryptoexchange.R
import br.com.cryptoexchange.domain.model.Exchange
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import java.text.DateFormat
import java.util.Locale
import androidx.compose.ui.tooling.preview.Preview
import br.com.cryptoexchange.ui.theme.CryptoExchangeTheme

@Composable
fun ExchangeRowItem(
    exchange: Exchange,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .semantics { contentDescription = "exchange_row_${exchange.id}" },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Logo — uses Coil SubcomposeAsyncImage for loading/error states
        // Coil has built-in disk + memory cache, equivalent to iOS CachedLogoView
        ExchangeLogo(logoUrl = exchange.logoUrl, sizeDp = 48)

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = exchange.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ShowChart,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = formatVolume(exchange.spotVolumeUsd),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
                Text(
                    text = formatDate(exchange),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )
    }
}

@Composable
fun ExchangeLogo(logoUrl: String?, sizeDp: Int, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape((sizeDp * 0.167f).dp)
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(logoUrl)
            .crossfade(true)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier
            .size(sizeDp.dp)
            .clip(shape),
        loading = {
            Box(
                modifier = Modifier
                    .size(sizeDp.dp)
                    .clip(shape),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size((sizeDp / 2).dp), strokeWidth = 2.dp)
            }
        },
        error = {
            Surface(
                modifier = Modifier.size(sizeDp.dp).clip(shape),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "🏦",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    )
}

// ─── Formatting helpers ──────────────────────────────────────────────────────

fun formatVolume(volume: Double?): String {
    if (volume == null) return "N/A"
    val billions = volume / 1_000_000_000.0
    if (billions >= 1) return "${"$%.2f".format(billions)}B"
    val millions = volume / 1_000_000.0
    if (millions >= 1) return "${"$%.2f".format(millions)}M"
    return "$${"%.0f".format(volume)}"
}

@Composable
fun formatDate(exchange: Exchange): String {
    val date = exchange.dateLaunched ?: return stringResource(R.string.not_available)
    val fmt = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
    return fmt.format(date)
}

// ─── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "ExchangeRowItem")
@Composable
private fun ExchangeRowItemPreview() {
    CryptoExchangeTheme {
        ExchangeRowItem(
            exchange = Exchange(
                id = 270,
                name = "Binance",
                slug = "binance",
                logoUrl = null,
                description = null,
                websiteUrl = null,
                dateLaunched = null,
                spotVolumeUsd = 12_345_678_901.23,
                makerFee = 0.001,
                takerFee = 0.001,
                weeklyVisits = 5_000_000,
                spot = 500
            )
        )
    }
}

@Preview(showBackground = true, name = "ExchangeLogo – Placeholder")
@Composable
private fun ExchangeLogoPreview() {
    CryptoExchangeTheme {
        ExchangeLogo(logoUrl = null, sizeDp = 64)
    }
}
