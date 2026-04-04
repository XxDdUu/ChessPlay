package com.sky.chessplay.ui.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sky.chessplay.domain.state.AuthState
import com.sky.chessplay.navigation.Route
import com.sky.chessplay.ui.presentation.auth.AuthScreen
import com.sky.chessplay.ui.presentation.auth.AuthViewModel
import com.sky.chessplay.ui.presentation.home.HomeScreen
import com.sky.chessplay.ui.presentation.offline_play.OfflinePlayScreen

@Composable
fun ChessPlayRoot() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsState()
    NavHost(
        navController = navController,
        startDestination = Route.Home.route
    ) {
        composable(Route.Home.route) {
            HomeScreen(
                onPlayClick = {
                    navController.navigate(Route.OfflinePlay.route)
                },
                onMultiplayerClick = {
                    if (authState is AuthState.Authenticated) {
                        navController.navigate(Route.MultiplayerOfflinePlay.route)
                    } else {
                        navController.navigate(Route.Auth.route)
                    }
                },
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
        composable(Route.Auth.route) {
            AuthScreen(
                onGoogleClick = {},
                onNextClick = {},
                onEmailChange = {},
                email = "",
                viewModel = authViewModel
            )
        }
    }
}
