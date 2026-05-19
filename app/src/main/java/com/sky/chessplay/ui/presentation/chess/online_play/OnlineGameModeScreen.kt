package com.sky.chessplay.ui.presentation.chess.online_play

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sky.chessplay.domain.state.AuthState
import com.sky.chessplay.navigation.Route
import com.sky.chessplay.ui.component.common.GradientButton
import com.sky.chessplay.ui.layout.AppScaffold
import com.sky.chessplay.ui.layout.AppScaffoldConfig
import com.sky.chessplay.ui.presentation.chess.online_play.modal.JoinRoomDialog
import com.sky.chessplay.ui.presentation.chess.online_play.modal.MatchMakingModal
import com.sky.chessplay.ui.presentation.chess.online_play.modal.RoomWaitingModal

@Composable
fun OnlineGameModeScreen(
    navController: NavHostController,
    matchViewModel: MatchViewModel,
    authState: AuthState
) {
    val navigateToGame = matchViewModel.navigateToGame
    val gameInit = matchViewModel.gameInitEvent

    LaunchedEffect(navigateToGame, gameInit) {
        if (navigateToGame && gameInit != null) {
            navController.navigate(Route.OnlinePlay.route) {
                popUpTo("online_mode") { inclusive = true }
            }

            matchViewModel.onNavigated()
        }
    }
    var showJoinDialog by remember {
        mutableStateOf(false)
    }
    AppScaffold(

        navController = navController,

        config = AppScaffoldConfig(
            showBottomBar = false,
            fab = null
        )

    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF0D0B2A),
                            Color(0xFF1A1440)
                        )
                    )
                )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {

                Spacer(Modifier.height(16.dp))

                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                }

                Spacer(Modifier.height(32.dp))

                Text(
                    text = "Online Play",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Connect with players worldwide",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    color = Color(0xFFB0B0C3)
                )

                Spacer(Modifier.height(40.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {

                    GradientButton(
                        title = "Auto Match",
                        subtitle = "Find opponents instantly",
                        colors = listOf(Color(0xFF22C55E), Color(0xFF16A34A)),
                        onClick = {
                            (authState as? AuthState.Authenticated)?.user?.let { user ->
                                matchViewModel.start(user.id)
                            }
                        }
                    )

                    GradientButton(
                        title = "Create Room",
                        subtitle = "Invite friends to join your game",
                        colors = listOf(Color(0xFF6D5BFF), Color(0xFF4F46E5)),
                        onClick = { matchViewModel.createRoom() }

                    )

                    GradientButton(
                        title = "Join Room",
                        subtitle = "Enter a room code",
                        colors = listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9)),
                        onClick = { showJoinDialog = true }
                    )
                }
            }
            MatchMakingModal(viewModel = matchViewModel)
            RoomWaitingModal(
                roomCode = matchViewModel.roomCode,
                status = matchViewModel.status,
                onCancel = {
                    matchViewModel.cancelSearch()
                }
            )
            if (showJoinDialog) {

                JoinRoomDialog(

                    roomCode = matchViewModel.joinRoomCode,

                    onRoomCodeChange = {
                        matchViewModel.updateJoinRoomCode(it)
                    },

                    onJoinClick = {
                        matchViewModel.joinRoom()
                        showJoinDialog = false
                    },

                    onDismiss = {
                        showJoinDialog = false
                    }
                )
            }
        }
    }
}
