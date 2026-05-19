package com.sky.chessplay.ui.root

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sky.chessplay.data.remote.GoogleAuthClient
import com.sky.chessplay.domain.state.AuthState
import com.sky.chessplay.navigation.Route
import com.sky.chessplay.ui.presentation.auth.AuthScreenRoute
import com.sky.chessplay.ui.presentation.auth.AuthViewModel
import com.sky.chessplay.ui.presentation.chess.ChessViewModel
import com.sky.chessplay.ui.presentation.chess.offline_play.OfflinePlayScreen
import com.sky.chessplay.ui.presentation.chess.online_play.MatchViewModel
import com.sky.chessplay.ui.presentation.chess.online_play.OnlineGameModeScreen
import com.sky.chessplay.ui.presentation.chess.online_play.OnlinePlayScreen
import com.sky.chessplay.ui.presentation.community.FriendRoute
import com.sky.chessplay.ui.presentation.home.HomeScreen

@Composable
fun ChessPlayRoot() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsState()
    val googleAuthClient = remember { GoogleAuthClient(context) }
    val matchViewModel: MatchViewModel = hiltViewModel()
    val chessViewModel: ChessViewModel = hiltViewModel()


    rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            googleAuthClient.handleSignInResult(
                intent = result.data,
                onSuccess = { idToken ->
                    authViewModel.signInWithGoogle(idToken)
                },
                onError = {}
            )
        }
    }

        NavHost(
            navController = navController,
            startDestination = Route.Home.route,
        ) {
            composable(Route.Home.route) {
                HomeScreen(
                    onPlayClick = {
                        navController.navigate(Route.OfflinePlay.route)
                    },
                    onMultiplayerClick = {
                        if (authState is AuthState.Authenticated) {
                            navController.navigate(Route.OnlineGameMode.route) {
                                popUpTo(Route.Auth.route) { inclusive = true }
                            }
                        } else {
                            navController.navigate(Route.Auth.route)
                        }
                    },
                    onSettingsClick = {},
                    navController = navController,
                    authState = authState,
                    onLogout =  { authViewModel.logout() }
                )
            }

            composable(Route.OfflinePlay.route) {
                OfflinePlayScreen()
            }

            composable(Route.MultiplayerOfflinePlay.route) {
                OfflinePlayScreen()
            }
            composable(Route.OnlineGameMode.route) {
                OnlineGameModeScreen(
                    navController = navController,
                    authState = authState,
                    matchViewModel = matchViewModel
                )
            }

            composable(Route.OnlinePlay.route) {
                OnlinePlayScreen(
                    viewModel = chessViewModel,
                    matchViewModel = matchViewModel,
                    navController = navController,
                    authState = authState
                )
            }
            composable(Route.Auth.route) {
                AuthScreenRoute(
                    navController = navController,
                    viewModel = authViewModel
                    )
            }
            composable(Route.Friend.route) {

                if (authState !is AuthState.Authenticated) {

                    LaunchedEffect(Unit) {
                        navController.navigate(Route.Auth.route) {
                            popUpTo(Route.Friend.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }

                    return@composable
                }

                val state = authState as AuthState.Authenticated

                FriendRoute(
                    userId = state.user.id,
                    onNavigateToDiscover = {
                        navController.navigate("friend_discover")
                    },
                    navController = navController
                )
            }
        }
}
