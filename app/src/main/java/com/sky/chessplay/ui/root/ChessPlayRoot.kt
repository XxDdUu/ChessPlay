package com.sky.chessplay.ui.root

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sky.chessplay.navigation.Route
import com.sky.chessplay.ui.presentation.home.HomeScreen
import com.sky.chessplay.ui.presentation.offline_play.OfflinePlayScreen

@Composable
fun ChessPlayRoot() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.Home.route
    ) {

        composable(Route.Home.route) {
            HomeScreen(
                onPlayClick = {
                    navController.navigate(Route.OfflinePlay.route)
                },
                onMultiplayerClick = {},
                onSettingsClick = {}
            )
        }

        composable(Route.OfflinePlay.route) {
            OfflinePlayScreen()
        }

        composable(Route.MultiplayerOfflinePlay.route) {
        }

        composable(Route.OnlinePlay.route) {
        }
    }
}
