package br.com.cryptoexchange.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.com.cryptoexchange.ui.screens.exchangedetail.ExchangeDetailScreen
import br.com.cryptoexchange.ui.screens.exchangelist.ExchangeListScreen

object NavArgs {
    const val EXCHANGE_ID = "exchangeId"
}

object NavRoutes {
    const val EXCHANGE_LIST = "exchange_list"
    const val EXCHANGE_DETAIL = "exchange_detail/{${NavArgs.EXCHANGE_ID}}"

    fun exchangeDetail(id: Int) = "exchange_detail/$id"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.EXCHANGE_LIST
    ) {
        composable(NavRoutes.EXCHANGE_LIST) {
            ExchangeListScreen(
                onExchangeClick = { exchange ->
                    navController.navigate(NavRoutes.exchangeDetail(exchange.id))
                }
            )
        }

        composable(
            route = NavRoutes.EXCHANGE_DETAIL,
            arguments = listOf(navArgument(NavArgs.EXCHANGE_ID) { type = NavType.IntType })
        ) {
            ExchangeDetailScreen(
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}
