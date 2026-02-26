package br.com.cryptoexchange.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import br.com.cryptoexchange.R
import br.com.cryptoexchange.domain.model.ExchangeMarketPair

@Composable
fun MarketPairRowItem(pair: ExchangeMarketPair, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            val pairLabel = "${pair.marketPairBase.currencySymbol}/${pair.marketPairQuote.currencySymbol}"
            Text(text = pairLabel, style = MaterialTheme.typography.titleMedium)
            Text(
                text = stringResource(R.string.volume_24h_label, formatPairVolume(pair.volumeUsd24h)),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.End
        ) {
            Text(
                text = formatPairPrice(pair.priceUsd),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.price_usd_label),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

private fun formatPairPrice(price: Double?): String {
    if (price == null) return "N/A"
    return if (price >= 1) "$%.2f".format(price) else "$%.6f".format(price)
}

private fun formatPairVolume(volume: Double?): String {
    if (volume == null) return "N/A"
    val millions = volume / 1_000_000.0
    return "$%.2fM".format(millions)
}
