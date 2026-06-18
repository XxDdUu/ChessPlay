package com.sky.chessplay.ui.presentation.tournament

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.sky.chessplay.domain.state.TournamentStatus
import com.sky.chessplay.navigation.Route
import com.sky.chessplay.ui.component.tournament.LeaderboardHeader
import com.sky.chessplay.ui.component.tournament.LeaderboardRow
import com.sky.chessplay.ui.component.tournament.PairingCard
import com.sky.chessplay.ui.component.tournament.TournamentSummaryCard
import com.sky.chessplay.ui.layout.AppScaffold
import com.sky.chessplay.ui.layout.AppScaffoldConfig
import com.sky.chessplay.ui.layout.TopBarAction

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentDetailScreen(
    tournamentId: Long,
    currentUserId: Long?,
    navController: NavHostController,
    viewModel: TournamentDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }
    val context = androidx.compose.ui.platform.LocalContext.current

    val lambdaRefresh = {
        viewModel.loadTournament(tournamentId)
        uiState.selectedRoundId?.let { roundId ->
            viewModel.loadPairings(tournamentId,roundId)
        }
        Unit
    }
    LaunchedEffect(tournamentId) {
        while (true) {
            viewModel.loadTournament(tournamentId)
            uiState.selectedRoundId?.let { roundId ->
                viewModel.loadPairings(tournamentId,roundId)
            }
            kotlinx.coroutines.delay(30000L)
        }
    }
    
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            android.widget.Toast.makeText(context, it, android.widget.Toast.LENGTH_LONG).show()
        }
    }

    AppScaffold(
        navController = navController,
        config = AppScaffoldConfig(
            showTopBar = true,
            showBottomBar = true,
            title = "Chi tiết giải đấu",
            actions = listOf(
                TopBarAction.Refresh(onClick = lambdaRefresh)
            )
        ),
    ) {
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }

            uiState.error != null && uiState.tournament == null -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = uiState.error ?: "Không thể tải thông tin giải đấu",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            else -> {
                val tournament = uiState.tournament

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF1C1A17)),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (tournament != null) {
                        item {
                            TournamentSummaryCard(
                                tournament = tournament,
                                isRegistered = uiState.standings.any {
                                    currentUserId != null && it.playerId == currentUserId
                                },
                                onJoinClick = { viewModel.joinTournament(tournamentId) },
                                onLeaveClick = { viewModel.leaveTournament(tournamentId) },
                                myPairing = uiState.myPairing,
                                onLobbyClick = {
                                    if (uiState.myPairing?.inBreak == true) {
                                        navController.navigate(Route.TournamentBreak.createRoute(tournamentId))
                                    } else {
                                        navController.navigate(Route.TournamentLobby.createRoute(tournamentId))
                                    }
                                }
                            )
                        }
                    }

                    item {
                        PrimaryTabRow(selectedTabIndex = selectedTab) {
                            Tab(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                text = { Text("Bảng xếp hạng") }
                            )
                            Tab(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                text = { Text("Vòng & Trận đấu") }
                            )
                        }
                    }

                    if (selectedTab == 0) {
                        if (uiState.standings.isEmpty()) {
                            item {
                                EmptyTournamentMessage("Chưa có người chơi nào đăng ký.")
                            }
                        } else {
                            item { LeaderboardHeader() }
                            itemsIndexed(
                                items = uiState.standings,
                                key = { _, standing -> standing.playerId }
                            ) { index, standing ->
                                LeaderboardRow(
                                    standing = standing,
                                    rank = standing.rank.takeIf { it > 0 } ?: index + 1,
                                    isCurrentUser = currentUserId != null &&
                                             standing.playerId == currentUserId
                                )
                            }
                        }
                    } else {
                        item {
                            RoundsSelector(
                                tournamentId = tournamentId,
                                selectedRoundId = uiState.selectedRoundId,
                                rounds = uiState.rounds,
                                onRoundClick = viewModel::loadPairings
                            )
                        }

                        when {
                            uiState.rounds.isEmpty() -> item {
                                val message = if (tournament?.status == TournamentStatus.REGISTERING) {
                                    "Các vòng đấu sẽ được tạo khi giải đấu bắt đầu."
                                } else {
                                    "Thông tin chi tiết về vòng đấu và trận đấu chưa có sẵn."
                                }
                                EmptyTournamentMessage(uiState.roundsError ?: message)
                            }

                            uiState.isPairingsLoading -> item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }

                            uiState.pairings.isEmpty() -> item {
                                EmptyTournamentMessage(
                                    uiState.roundsError ?: "Không có trận đấu nào trong vòng này."
                                )
                            }

                            else -> items(
                                items = uiState.pairings,
                                key = { it.id }
                            ) { pairing ->
                                PairingCard(pairing = pairing)
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
private fun RoundsSelector(
    tournamentId: Long,
    selectedRoundId: Long?,
    rounds: List<com.sky.chessplay.domain.model.tournament.TournamentRound>,
    onRoundClick: (Long, Long) -> Unit
) {
    if (rounds.isEmpty()) return

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 2.dp)
    ) {
        items(rounds, key = { it.id }) { round ->
            FilterChip(
                selected = selectedRoundId == round.id,
                onClick = { onRoundClick(tournamentId, round.id) },
                label = { Text("Vòng ${round.number}") }
            )
        }
    }
}

@Composable
private fun EmptyTournamentMessage(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF262421)),
        border = BorderStroke(1.dp, Color(0xFF312E2B)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = message,
                modifier = Modifier.align(Alignment.Center),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
