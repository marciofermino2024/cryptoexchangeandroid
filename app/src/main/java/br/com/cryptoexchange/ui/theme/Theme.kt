package br.com.cryptoexchange.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AccentColor = Color(0xFF007AFF) // iOS accent blue

private val LightColors = lightColorScheme(
    primary = AccentColor,
    onPrimary = Color.White,
    secondary = AccentColor,
    background = Color.White,
    surface = Color.White
)

private val DarkColors = darkColorScheme(
    primary = AccentColor,
    onPrimary = Color.White,
    secondary = AccentColor,
    background = Color(0xFF1C1C1E),
    surface = Color(0xFF2C2C2E)
)

@Composable
fun CryptoExchangeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(colorScheme = colors, content = content)
}
