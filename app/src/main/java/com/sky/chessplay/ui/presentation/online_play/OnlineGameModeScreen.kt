package com.sky.chessplay.ui.presentation.online_play

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sky.chessplay.ui.component.common.GradientButton
import com.sky.chessplay.ui.layout.AppScaffold

@Composable
fun OnlineGameModeScreen(
    navController: NavHostController,
    onAutoMatch: () -> Unit,
    onCreateRoom: () -> Unit,
    onJoinRoom: () -> Unit
) {
    AppScaffold(
        navController = navController,
        showBottomBar = false,
        showFab = false
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
                        onClick = onAutoMatch
                    )

                    GradientButton(
                        title = "Create Room",
                        subtitle = "Invite friends to join your game",
                        colors = listOf(Color(0xFF6D5BFF), Color(0xFF4F46E5)),
                        onClick = onCreateRoom
                    )

                    GradientButton(
                        title = "Join Room",
                        subtitle = "Enter a room code",
                        colors = listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9)),
                        onClick = onJoinRoom
                    )
                }
            }
        }
    }
}
