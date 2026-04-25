package com.sky.chessplay.ui.root

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.sky.chessplay.data.remote.GoogleAuthClient
import com.sky.chessplay.domain.state.AuthState
import com.sky.chessplay.navigation.Route
import com.sky.chessplay.ui.presentation.auth.AuthScreen
import com.sky.chessplay.ui.presentation.auth.AuthViewModel
import com.sky.chessplay.ui.presentation.home.HomeScreen
import com.sky.chessplay.ui.presentation.offline_play.OfflinePlayScreen
import com.sky.chessplay.ui.presentation.online_play.OnlineGameModeScreen

@Composable
fun ChessPlayRoot() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsState()
    val googleAuthClient = remember { GoogleAuthClient(context) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result -> val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

        try {
            val account = task.getResult(ApiException::class.java)
        } catch (e: ApiException) {
            Log.e("Auth", "API ERROR CODE: ${e.statusCode}")
        }
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
                            navController.navigate(Route.OnlineGameMode.route)
                        } else {
                            navController.navigate(Route.Auth.route)
                        }
                    },
                    onSettingsClick = {},
                    navController = navController
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
                    onJoinRoom = {},
                    onAutoMatch = {},
                    onCreateRoom = {}
                )
            }

            composable(Route.OnlinePlay.route) {
            }
            composable(Route.Auth.route) {
                AuthScreen(
                    onGoogleClick = {
                        Log.d("Auth", "CLICK GOOGLE")
                        launcher.launch(googleAuthClient.getSignInIntent())
                    },
                    onNextClick = {},
                    onEmailChange = {},
                    email = "",
                    viewModel = authViewModel
                )
            }
        }
}
