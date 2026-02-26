package br.com.cryptoexchange

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import br.com.cryptoexchange.ui.navigation.AppNavigation
import br.com.cryptoexchange.ui.theme.CryptoExchangeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CryptoExchangeTheme {
                AppNavigation()
            }
        }
    }
}
