package com.sky.chessplay.ui.root

import AiPlayRoute
import android.app.Activity
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sky.chessplay.data.remote.GoogleAuthClient
import com.sky.chessplay.domain.model.auth.Role
import com.sky.chessplay.domain.state.AuthState
import com.sky.chessplay.navigation.Route
import com.sky.chessplay.ui.presentation.admin.AdminScreen
import com.sky.chessplay.ui.presentation.auth.AuthScreenRoute
import com.sky.chessplay.ui.presentation.auth.AuthViewModel
import com.sky.chessplay.ui.presentation.chess.ChessViewModel
import com.sky.chessplay.ui.presentation.chess.offline_play.OfflinePlayScreen
import com.sky.chessplay.ui.presentation.chess.online_play.MatchViewModel
import com.sky.chessplay.ui.presentation.chess.online_play.OnlineGameModeScreen
import com.sky.chessplay.ui.presentation.chess.online_play.OnlinePlayScreen
import com.sky.chessplay.ui.presentation.community.FriendRoute
import com.sky.chessplay.ui.presentation.home.HomeScreen
import com.sky.chessplay.ui.presentation.home.HomeViewModel
import com.sky.chessplay.ui.presentation.profile.ProfileRoute
import com.sky.chessplay.ui.presentation.tournament.TournamentDetailScreen
import com.sky.chessplay.ui.presentation.tournament.TournamentRoute
import com.sky.chessplay.ui.presentation.tournament.TournamentStandingsScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChessPlayRoot() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsState()
    val googleAuthClient = remember { GoogleAuthClient(context) }
    val matchViewModel: MatchViewModel = hiltViewModel()
    val chessViewModel: ChessViewModel = hiltViewModel()
    val homeViewModel: HomeViewModel = hiltViewModel()
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
                val savedStateHandle =
                    navController.currentBackStackEntry?.savedStateHandle

                val shouldOpenAi =
                    savedStateHandle?.get<Boolean>("open_ai_after_auth") == true

                val authSuccess =
                    savedStateHandle?.get<Boolean>("auth_success") == true

                LaunchedEffect(authSuccess, shouldOpenAi) {

                    if (authSuccess && shouldOpenAi) {

                        homeViewModel.openAiModal()

                        savedStateHandle.remove<Boolean>("auth_success")
                        savedStateHandle.remove<Boolean>("open_ai_after_auth")
                    }
                }
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
                    onLogout =  { authViewModel.logout() },
                    showAiModal = homeViewModel.showAiModal,

                    aiModels = homeViewModel.aiModels,
                    selectedModel = homeViewModel.selectedModel,
                    difficulty = homeViewModel.difficulty,
                    playerColor = homeViewModel.playerColor,

                    isLoading = homeViewModel.isLoading,

                    onOpenAiModal = {
                        if (authState is AuthState.Authenticated) {
                            homeViewModel.openAiModal()
                        } else {

                            navController.currentBackStackEntry
                                ?.savedStateHandle
                                ?.set("open_ai_after_auth", true)

                            navController.navigate(Route.Auth.route)
                        }
                    },
                    onDismissAiModal = homeViewModel::closeAiModal,

                    onSelectModel = homeViewModel::selectModel,
                    onDifficultyChange = homeViewModel::changeDifficulty,
                    onPlayerColorChange = homeViewModel::changePlayerColor,
                    incomingInvite = matchViewModel.incomingInvite,
                    onAcceptInvite = { hostId ->
                        matchViewModel.acceptInvite(hostId)
                    },

                    onDismissInvite = {
                        matchViewModel.clearInvite()
                    },
                    onStartAiGame = {

                        val config = homeViewModel.buildAiConfig()

                        navController.navigate(
                            Route.AIPlay.createRoute(
                                model = config.model,
                                difficulty = config.difficulty,
                                side = config.side.name
                            )
                        )
                    },
                    onTournamentClick = {
                        navController.navigate(Route.Tournament.route)
                    },
                    onAdminClick = {
                        if (authState is AuthState.Authenticated) {
                            navController.navigate(Route.Admin.route)
                        } else {
                            navController.navigate(Route.Auth.route)
                        }
                    }

                )
                    val navigateToGame = matchViewModel.navigateToGame
                    val gameInit = matchViewModel.gameInitEvent

                    LaunchedEffect(navigateToGame, gameInit) {
                        if (navigateToGame && gameInit != null) {
                            matchViewModel.resetMatchState()

                            navController.navigate(Route.OnlinePlay.route) {
                                popUpTo("online_mode") { inclusive = true }
                            }

                            matchViewModel.onNavigated()
                        }
                    }
                }

            composable(Route.OfflinePlay.route) {
                OfflinePlayScreen(navController = navController)
            }

            composable(Route.MultiplayerOfflinePlay.route) {
                OfflinePlayScreen(navController = navController)
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
            composable(Route.Profile.route) {
                if (authState !is AuthState.Authenticated) {
                    LaunchedEffect(Unit) {
                        navController.navigate(Route.Auth.route) {
                            popUpTo(Route.Profile.route) { inclusive = true }
                        }
                    }

                    return@composable
                }

                ProfileRoute(navController = navController, authViewModel = authViewModel)
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
            composable(
                route = Route.AIPlay.route,

                arguments = listOf(

                    navArgument("model") {
                        type = NavType.StringType
                    },

                    navArgument("difficulty") {
                        type = NavType.IntType
                    },

                    navArgument("side") {
                        type = NavType.StringType
                    }
                )
            ) {
                AiPlayRoute(
                    navController = navController
                )
            }
            composable(
                route = Route.Tournament.route
            ) {
                if (authState !is AuthState.Authenticated) {
                    LaunchedEffect(Unit) {
                        navController.navigate(Route.Auth.route) {
                            popUpTo(Route.Tournament.route) { inclusive = true }
                        }
                    }
                    return@composable
                }
                val currentUserId = (authState as? AuthState.Authenticated)?.user?.id

                TournamentRoute(
                    navController = navController,
                    currentUserId = currentUserId
                )
            }

            composable(
                route = Route.TournamentStandings.route,
                arguments = listOf(
                    navArgument("tournamentId") {
                        type = NavType.LongType
                    }
                )
            ) { backStackEntry ->

                val tournamentId = backStackEntry.arguments?.getLong("tournamentId") ?: 0L
                TournamentStandingsScreen(
                    tournamentId = tournamentId
                )
            }

            composable(
                route = Route.TournamentDetail.route,
                arguments = listOf(
                    navArgument("tournamentId") {
                        type = NavType.LongType
                    }
                )
            ) { backStackEntry ->
                val tournamentId = backStackEntry.arguments?.getLong("tournamentId") ?: 0L
                val currentUserId = (authState as? AuthState.Authenticated)?.user?.id

                TournamentDetailScreen(
                    tournamentId = tournamentId,
                    currentUserId = currentUserId,
                    navController = navController
                )
            }

            composable(
                route = Route.Admin.route
            ) {
                val adminUser = (authState as? AuthState.Authenticated)?.user
                if (adminUser == null || adminUser.role != Role.ROLE_ADMIN) {
                    LaunchedEffect(Unit) {
                        navController.navigate(Route.Home.route) {
                            popUpTo(Route.Admin.route) { inclusive = true }
                        }
                    }
                    return@composable
                }

                AdminScreen(
                    navController = navController
                )
            }
        }
}
