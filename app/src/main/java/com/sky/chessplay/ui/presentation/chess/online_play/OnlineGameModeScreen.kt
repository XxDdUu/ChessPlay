package com.sky.chessplay.ui.presentation.chess.online_play

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.navigation.NavHostController
import com.sky.chessplay.domain.state.AuthState
import com.sky.chessplay.navigation.Route
import com.sky.chessplay.ui.component.common.GradientButton
import com.sky.chessplay.ui.layout.AppScaffold
import com.sky.chessplay.ui.layout.AppScaffoldConfig
import com.sky.chessplay.ui.presentation.chess.online_play.modal.JoinRoomDialog
import com.sky.chessplay.ui.presentation.chess.online_play.modal.MatchMakingModal
import com.sky.chessplay.ui.presentation.chess.online_play.modal.RoomWaitingModal

private data class TimeControlOption(
    val type: String,
    val label: String,
    val icon: ImageVector,
    val subtitle: String
)

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
            matchViewModel.resetMatchState()

            navController.navigate(Route.OnlinePlay.route) {
                popUpTo("online_mode") { inclusive = true }
            }

            matchViewModel.onNavigated()
        }
    }
    var showJoinDialog by remember {
        mutableStateOf(false)
    }
    var showMatchTypeDialog by remember {
        mutableStateOf(false)
    }
    var selectedMatchType by remember {
        mutableStateOf("RAPID")
    }

    val timeControlOptions = listOf(
        TimeControlOption("BULLET", "Bullet", Icons.Default.Whatshot, "1 + 0"),
        TimeControlOption("BLITZ", "Blitz", Icons.Default.FlashOn, "5 + 0"),
        TimeControlOption("RAPID", "Rapid", Icons.Default.Timer, "10 + 0"),
        TimeControlOption("CLASSICAL", "Classical", Icons.Default.Schedule, "30 + 0")
    )

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
                            showMatchTypeDialog = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    GradientButton(
                        title = "Create Room",
                        subtitle = "Invite friends to join your game",
                        colors = listOf(Color(0xFF6D5BFF), Color(0xFF4F46E5)),
                        onClick = { matchViewModel.createRoom() },
                        modifier = Modifier.fillMaxWidth()
                    )

                    GradientButton(
                        title = "Join Room",
                        subtitle = "Enter a room code",
                        colors = listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9)),
                        onClick = { showJoinDialog = true },
                        modifier = Modifier.fillMaxWidth()
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
            if (showMatchTypeDialog) {
                MatchTimeDialog(
                    selectedType = selectedMatchType,
                    timeControlOptions = timeControlOptions,
                    onSelectType = { selectedMatchType = it },
                    onConfirm = {
                        showMatchTypeDialog = false
                        (authState as? AuthState.Authenticated)?.user?.let { user ->
                            matchViewModel.start(user.id, selectedMatchType)
                        }
                    },
                    onDismiss = {
                        showMatchTypeDialog = false
                    }
                )
            }

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

@Composable
private fun MatchTimeDialog(
    selectedType: String,
    timeControlOptions: List<TimeControlOption>,
    onSelectType: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1440))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Choose time control",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Select the play time for random matchmaking",
                    color = Color(0xFFB0B0C3),
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(20.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    timeControlOptions.forEach { option ->
                        val isSelected = option.type == selectedType
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectType(option.type) }
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) Color(0xFF22C55E) else Color(0xFF3F3C6B),
                                    shape = RoundedCornerShape(18.dp)
                                ),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0xFF272458) else Color(0xFF171332)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = option.icon,
                                    contentDescription = option.label,
                                    tint = if (isSelected) Color(0xFF22C55E) else Color(0xFF8B8ACA),
                                    modifier = Modifier.size(28.dp)
                                )

                                Spacer(Modifier.width(14.dp))

                                Column {
                                    Text(
                                        text = option.label,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = option.subtitle,
                                        color = Color(0xFFB0B0C3),
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E))
                    ) {
                        Text("Start")
                    }
                }
            }
        }
    }
}
