package com.sky.chessplay.ui.presentation.tournament

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.sky.chessplay.domain.model.tournament.Standing
import com.sky.chessplay.domain.model.tournament.Tournament
import com.sky.chessplay.domain.model.tournament.TournamentPairing
import com.sky.chessplay.domain.state.TournamentStatus
import com.sky.chessplay.ui.component.tournament.TournamentStatusChip
import com.sky.chessplay.ui.layout.AppScaffold
import com.sky.chessplay.ui.layout.AppScaffoldConfig
import com.sky.chessplay.navigation.Route
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.width

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

    LaunchedEffect(tournamentId) {
        viewModel.loadTournament(tournamentId)
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
            title = "Tournament Detail"
        )
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
                        text = uiState.error ?: "Failed to load tournament",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            else -> {
                val tournament = uiState.tournament

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
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
                                text = { Text("Leaderboard") }
                            )
                            Tab(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                text = { Text("Rounds & Matches") }
                            )
                        }
                    }

                    if (selectedTab == 0) {
                        if (uiState.standings.isEmpty()) {
                            item {
                                EmptyTournamentMessage("No players registered yet.")
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
                                    "Rounds will be generated when the tournament starts."
                                } else {
                                    "Round and match details are not available yet."
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
                                    uiState.roundsError ?: "No matches in this round."
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun TournamentSummaryCard(
    tournament: Tournament,
    isRegistered: Boolean,
    onJoinClick: () -> Unit,
    onLeaveClick: () -> Unit,
    myPairing: com.sky.chessplay.domain.model.tournament.MyPairing? = null,
    onLobbyClick: () -> Unit = {}
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tournament.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = tournament.description.ifBlank { "No description." },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                TournamentStatusChip(status = tournament.status.name)
            }

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DetailStat(
                    label = "Time",
                    value = tournament.timeControl,
                    modifier = Modifier.weight(1f)
                )
                DetailStat(
                    label = "Rounds",
                    value = tournament.totalRounds.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DetailStat(
                    label = "Registration Start",
                    value = formatTournamentDate(tournament.registrationStart),
                    modifier = Modifier.weight(1f)
                )
                DetailStat(
                    label = "Registration End",
                    value = formatTournamentDate(tournament.registrationEnd),
                    modifier = Modifier.weight(1f)
                )
            }

            DetailStat(
                label = "Tournament Start",
                value = formatTournamentDate(tournament.startTime),
                modifier = Modifier.fillMaxWidth()
            )

            if (tournament.status == TournamentStatus.REGISTERING) {
                if (isRegistered) {
                    OutlinedButton(
                        onClick = onLeaveClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Leave Tournament")
                    }
                } else {
                    Button(
                        onClick = onJoinClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Join Tournament")
                    }
                }
            } else if (tournament.status == TournamentStatus.ONGOING && isRegistered) {
                if (myPairing != null) {
                    val isBreak = myPairing.inBreak
                    Button(
                        onClick = onLobbyClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isBreak) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isBreak) "Giải lao giữa hiệp (Round ${myPairing.roundNumber})" else "Vào phòng chờ thi đấu (Round ${myPairing.roundNumber})")
                    }
                } else {
                    Text(
                        text = "Đang đợi hệ thống ghép cặp vòng đấu mới...",
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailStat(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun LeaderboardHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("#", modifier = Modifier.weight(0.5f), fontWeight = FontWeight.Bold)
        Text("Player", modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold)
        Text("Elo", modifier = Modifier.weight(0.9f), fontWeight = FontWeight.Bold)
        Text("Pts", modifier = Modifier.weight(0.8f), fontWeight = FontWeight.Bold)
        Text("BH", modifier = Modifier.weight(0.8f), fontWeight = FontWeight.Bold)
        Text("SB", modifier = Modifier.weight(0.8f), fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun LeaderboardRow(
    standing: Standing,
    rank: Int,
    isCurrentUser: Boolean
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("#$rank", modifier = Modifier.weight(0.5f), fontWeight = FontWeight.Bold)
            Text(
                text = if (isCurrentUser) "${standing.username} (You)" else standing.username,
                modifier = Modifier.weight(2f),
                fontWeight = FontWeight.SemiBold
            )
            Text(standing.initialRating.toString(), modifier = Modifier.weight(0.9f))
            Text(standing.currentScore.toString(), modifier = Modifier.weight(0.8f))
            Text(standing.buchholz.toString(), modifier = Modifier.weight(0.8f))
            Text(standing.sonnebornBerger.toString(), modifier = Modifier.weight(0.8f))
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
                label = { Text("Round ${round.number}") }
            )
        }
    }
}

@Composable
private fun PairingCard(pairing: TournamentPairing) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PlayerColumn(
                    name = pairing.whitePlayerName,
                    rating = pairing.whitePlayerRating,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = if (pairing.isBye) {
                        "BYE"
                    } else {
                        pairing.result.ifBlank { "vs" }
                    },
                    modifier = Modifier.weight(0.7f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                PlayerColumn(
                    name = pairing.blackPlayerName.ifBlank { "No opponent" },
                    rating = pairing.blackPlayerRating,
                    modifier = Modifier.weight(1f),
                    alignEnd = true
                )
            }

            if (pairing.gameId != null) {
                Text(
                    text = "Game #${pairing.gameId}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
private fun PlayerColumn(
    name: String,
    rating: Int?,
    modifier: Modifier = Modifier,
    alignEnd: Boolean = false
) {
    Column(
        modifier = modifier,
        horizontalAlignment = if (alignEnd) Alignment.End else Alignment.Start
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            textAlign = if (alignEnd) TextAlign.End else TextAlign.Start
        )
        if (rating != null && rating > 0) {
            Text(
                text = rating.toString(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EmptyTournamentMessage(message: String) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
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

@RequiresApi(Build.VERSION_CODES.O)
private fun formatTournamentDate(dateString: String): String {
    if (dateString.isBlank()) return "Chưa thiết lập"
    return try {
        val parsedDate = ZonedDateTime.parse(dateString).withZoneSameInstant(java.time.ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.getDefault())
        parsedDate.format(formatter)
    } catch (e: Exception) {
        try {
            val parsed = java.time.OffsetDateTime.parse(dateString).atZoneSameInstant(java.time.ZoneId.systemDefault())
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.getDefault())
            parsed.format(formatter)
        } catch (ex: Exception) {
            dateString
        }
    }
}
