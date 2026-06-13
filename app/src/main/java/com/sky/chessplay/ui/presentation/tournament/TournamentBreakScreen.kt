package com.sky.chessplay.ui.presentation.tournament

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.sky.chessplay.navigation.Route
import com.sky.chessplay.ui.component.tournament.StandingItem
import com.sky.chessplay.ui.layout.AppScaffold
import com.sky.chessplay.ui.layout.AppScaffoldConfig
import kotlinx.coroutines.delay

@Composable
fun TournamentBreakScreen(
    tournamentId: Long,
    breakDurationSeconds: Int,
    navController: NavHostController,
    viewModel: TournamentViewModel = hiltViewModel(),
    breakViewModel: TournamentBreakViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val nextRoundStarted by breakViewModel.nextRoundStarted.collectAsStateWithLifecycle()

    val breakTimeLeft by breakViewModel.breakTimeLeft.collectAsStateWithLifecycle()

    var timeLeft by remember { mutableIntStateOf(breakDurationSeconds) }

    LaunchedEffect(tournamentId) {
        viewModel.loadStandings(tournamentId)
        breakViewModel.loadBreakTime(tournamentId)
    }

    LaunchedEffect(breakTimeLeft) {
        breakTimeLeft?.let {
            timeLeft = it.toInt()
        }
    }

    // Countdown Timer logic
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            if (timeLeft > 0) {
                timeLeft -= 1
            }
        }
    }

    // Auto-navigation back to match lobby
    LaunchedEffect(nextRoundStarted, timeLeft) {
        if (nextRoundStarted || timeLeft <= 0) {
            navController.navigate(Route.TournamentLobby.createRoute(tournamentId)) {
                popUpTo(Route.TournamentBreak.createRoute(tournamentId, breakDurationSeconds)) { inclusive = true }
            }
        }
    }

    AppScaffold(
        navController = navController,
        config = AppScaffoldConfig(
            showTopBar = true,
            showBottomBar = false,
            title = "Round Break"
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
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Round Break Title
                Text(
                    text = "ROUND COMPLETED",
                    color = Color(0xFF00FFCC),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(top = 16.dp)
                )

                Text(
                    text = "Take a break, next round starts soon!",
                    color = Color.LightGray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Countdown clock ring
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0x1AFFFFFF)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val minutes = timeLeft / 60
                        val seconds = timeLeft % 60
                        val timeStr = String.format("%02d:%02d", minutes, seconds)

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(120.dp)
                        ) {
                            CircularProgressIndicator(
                                progress = { timeLeft.toFloat() / breakDurationSeconds.toFloat() },
                                modifier = Modifier.fillMaxSize(),
                                color = Color(0xFF00FFCC),
                                strokeWidth = 6.dp,
                                trackColor = Color(0x11FFFFFF)
                            )
                            Text(
                                text = timeStr,
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Next round pairings will generate automatically when break ends.",
                            color = Color.LightGray,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Standings / Leaderboard Title
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Current Leaderboard",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Standings",
                        color = Color(0xFF00FFCC),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Leaderboard list
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF00FFCC))
                    }
                } else if (uiState.error != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiState.error ?: "Failed to load standings",
                            color = Color.LightGray
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.standings) { standing ->
                            StandingItem(standing = standing)
                        }
                    }
                }

                // Action button to view details manually
                Button(
                    onClick = {
                        navController.navigate(Route.TournamentDetail.createRoute(tournamentId))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x33FFFFFF)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(top = 8.dp)
                ) {
                    Text(
                        text = "VIEW TOURNAMENT DETAILS",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
