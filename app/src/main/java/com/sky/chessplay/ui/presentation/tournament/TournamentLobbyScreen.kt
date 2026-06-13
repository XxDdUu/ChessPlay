package com.sky.chessplay.ui.presentation.tournament

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sky.chessplay.navigation.Route
import com.sky.chessplay.ui.layout.AppScaffold
import com.sky.chessplay.ui.layout.AppScaffoldConfig

@Composable
fun TournamentLobbyScreen(
    tournamentId: Long,
    currentUserId: Long?,
    navController: NavHostController,
    viewModel: TournamentLobbyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val timeLeft by viewModel.timeLeft.collectAsState()

    LaunchedEffect(tournamentId) {
        viewModel.loadLobby(tournamentId)
    }

    LaunchedEffect(uiState.gameIdToNavigate) {
        uiState.gameIdToNavigate?.let {
            navController.navigate(Route.OnlinePlay.createRoute(isTournament = true, tournamentId = tournamentId)) {
                popUpTo(Route.TournamentLobby.createRoute(tournamentId)) { inclusive = true }
            }
        }
    }

    LaunchedEffect(uiState.nextRoundNumberToNavigate) {
        uiState.nextRoundNumberToNavigate?.let {
            navController.navigate(Route.TournamentBreak.createRoute(tournamentId, uiState.breakDurationSeconds)) {
                popUpTo(Route.TournamentLobby.createRoute(tournamentId)) { inclusive = true }
            }
        }
    }

    AppScaffold(
        navController = navController,
        config = AppScaffoldConfig(
            showTopBar = true,
            showBottomBar = false,
            title = "Match Lobby"
        )
    ) {
        val darkBgGradient = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF0F2027),
                Color(0xFF203A43),
                Color(0xFF2C5364)
            )
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(darkBgGradient)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(color = Color(0xFF00FFCC))
                }

                uiState.error != null && uiState.pairing == null -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Error",
                            tint = Color(0xFFFF3366),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = uiState.error ?: "Failed to load lobby",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                uiState.pairing == null -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0x33FFFFFF)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    ) {
                        Text(
                            text = "Waiting for pairings to be generated...",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(24.dp).fillMaxWidth()
                        )
                    }
                }

                uiState.isCompleted -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0x1A00FFCC)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .border(1.dp, Color(0x3300FFCC), RoundedCornerShape(16.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Completed",
                                tint = Color(0xFF00FFCC),
                                modifier = Modifier.size(64.dp)
                            )
                            Text(
                                text = "ROUND COMPLETED",
                                color = Color(0xFF00FFCC),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = uiState.completedMessage ?: "You have completed your match.",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Waiting for other matches to finish. Please stay on this screen to start the next round.",
                                color = Color.LightGray,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { navController.navigate(Route.TournamentDetail.createRoute(tournamentId)) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FFCC)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(48.dp)
                            ) {
                                Text(
                                    text = "VIEW STANDINGS",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                else -> {
                    val pairing = uiState.pairing!!

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Header / Round Number
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text(
                                text = "ROUND ${pairing.roundNumber}",
                                color = Color(0xFF00FFCC),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                            Text(
                                text = if (pairing.isBye) "You have a BYE this round!" else "Tournament Match Check-in",
                                color = Color.LightGray,
                                fontSize = 16.sp
                            )
                        }

                        // VS Players Layout
                        if (!pairing.isBye) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Player 1 (You)
                                PlayerCheckInCard(
                                    name = "You",
                                    rating = pairing.opponentRating, // Just placeholder rating
                                    color = pairing.myColor,
                                    isReady = pairing.iAmReady,
                                    modifier = Modifier.weight(1f)
                                )

                                // VS Badge
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .background(Color(0xFFFF3366), CircleShape)
                                        .border(2.dp, Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "VS",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }

                                // Player 2 (Opponent)
                                PlayerCheckInCard(
                                    name = pairing.opponentName,
                                    rating = pairing.opponentRating,
                                    color = if (pairing.myColor == "WHITE") "BLACK" else "WHITE",
                                    isReady = pairing.opponentReady,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // Forfeit / Status Warnings
                        if (uiState.isForfeited) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0x66FF3366)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                            ) {
                                Text(
                                    text = uiState.forfeitMessage ?: "Match Forfeited",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(16.dp).fillMaxWidth()
                                )
                            }
                        }

                        // Countdown clock
                        if (!pairing.isBye && !uiState.isForfeited) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(vertical = 16.dp)
                            ) {
                                val minutes = timeLeft / 60
                                val seconds = timeLeft % 60
                                val timeStr = String.format("%02d:%02d", minutes, seconds)

                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.size(140.dp)
                                ) {
                                    CircularProgressIndicator(
                                        progress = { timeLeft.toFloat() / pairing.lobbyTimeLimitSeconds.toFloat() },
                                        modifier = Modifier.fillMaxSize(),
                                        color = if (timeLeft < 60) Color(0xFFFF3366) else Color(0xFF00FFCC),
                                        strokeWidth = 8.dp,
                                        trackColor = Color(0x22FFFFFF)
                                    )
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = timeStr,
                                            color = Color.White,
                                            fontSize = 32.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Check-in",
                                            color = Color.LightGray,
                                            fontSize = 12.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "Failure to check-in within 5 minutes results in immediate forfeit.",
                                    color = Color(0xFFFFB300),
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 24.dp)
                                )
                            }
                        }

                        // Ready/Check-in action button
                        if (pairing.isBye) {
                            Button(
                                onClick = { navController.navigate(Route.TournamentDetail.createRoute(tournamentId)) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FFCC)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .padding(bottom = 16.dp)
                             ) {
                                Text(
                                    text = "GO TO STANDINGS",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        } else if (!uiState.isForfeited) {
                            Button(
                                onClick = { viewModel.markReady() },
                                enabled = !pairing.iAmReady,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (pairing.iAmReady) Color(0x3300FFCC) else Color(0xFF00FFCC),
                                    disabledContainerColor = Color(0x3300FFCC)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .padding(bottom = 16.dp)
                            ) {
                                if (pairing.iAmReady) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Checked In",
                                        tint = Color(0xFF00FFCC),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "READY & CHECKED IN",
                                        color = Color(0xFF00FFCC),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                } else {
                                    Text(
                                        text = "CHECK IN (READY)",
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        } else {
                            // Go back button when forfeited
                            Button(
                                onClick = { navController.navigate(Route.TournamentDetail.createRoute(tournamentId)) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3366)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .padding(bottom = 16.dp)
                            ) {
                                Text(
                                    text = "RETURN TO TOURNAMENT",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerCheckInCard(
    name: String,
    rating: Int,
    color: String,
    isReady: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .border(
                width = 2.dp,
                color = if (isReady) Color(0xFF00FFCC) else Color(0x22FFFFFF),
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x1AFFFFFF))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Rating: $rating",
                color = Color.LightGray,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Color: $color",
                color = if (color == "WHITE") Color.White else Color.DarkGray,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                modifier = Modifier
                    .background(
                        if (color == "WHITE") Color(0x22FFFFFF) else Color(0xDDFFFFFF),
                        RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Ready light indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(
                            color = if (isReady) Color(0xFF00FFCC) else Color(0xFFFF3366),
                            shape = CircleShape
                        )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isReady) "READY" else "NOT READY",
                    color = if (isReady) Color(0xFF00FFCC) else Color(0xFFFF3366),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
