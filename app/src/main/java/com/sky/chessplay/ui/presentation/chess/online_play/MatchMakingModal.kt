package com.sky.chessplay.ui.presentation.chess.online_play

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sky.chessplay.domain.state.MatchState

@Composable
fun MatchMakingModal(viewModel: MatchViewModel) {

    val state = viewModel.matchState
    val opponent = viewModel.opponent
    val confirmCountdown = viewModel.confirmCountdown
    val countdown = viewModel.countdown
    val status = viewModel.status

    if (state == MatchState.INITIALIZING) return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center
    ) {

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                when (state) {

                    MatchState.SEARCHING -> {
                        CircularProgressIndicator(color = Color.White)

                        Spacer(Modifier.height(16.dp))

                        Text("Finding opponent...", color = Color.White)

                        Spacer(Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.cancelSearch() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text("Cancel")
                        }
                    }

                    MatchState.FOUND -> {
                        Text("Opponent Found!", color = Color.White)

                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = opponent?.username ?: "Unknown",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(6.dp))

                        Text(
                            text = opponent?.countryCode ?: "🌍",
                            color = Color.LightGray
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = "Accept in ${confirmCountdown}s",
                            color = if (confirmCountdown <= 3) Color.Red else Color.LightGray
                        )

                        Spacer(Modifier.height(16.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                            Button(
                                onClick = { viewModel.acceptMatch() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E))
                            ) {
                                Text("Accept")
                            }

                            Button(
                                onClick = { viewModel.rejectMatch() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                            ) {
                                Text("Reject")
                            }
                        }
                    }

                    MatchState.COUNTDOWN -> {
                        Text("Game starting...", color = Color.White)

                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = "$countdown",
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (countdown <= 2) Color.Red else Color.White
                        )
                    }

                    MatchState.CANCELLED -> {
                        Text(
                            text = status,
                            color = Color.White
                        )
                    }

                    else -> {}
                }
            }
        }
    }
}