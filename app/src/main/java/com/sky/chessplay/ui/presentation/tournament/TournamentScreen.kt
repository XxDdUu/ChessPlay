package com.sky.chessplay.ui.presentation.tournament

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.sky.chessplay.ui.component.tournament.TournamentItem
import com.sky.chessplay.ui.component.tournament.TournamentRow
import com.sky.chessplay.ui.component.tournament.TournamentTableHeader
import com.sky.chessplay.ui.layout.AppScaffold
import com.sky.chessplay.ui.layout.AppScaffoldConfig

@Composable
fun TournamentScreen(
    viewModel: TournamentViewModel = hiltViewModel(),
    currentUserId: Long?,
    onTournamentClick: (Long) -> Unit,
    navController: NavHostController
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    AppScaffold(
        navController = navController,
        config = AppScaffoldConfig(
            showTopBar = true,
            showBottomBar = true,
            title = "Tournaments"
        )
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            AnimatedContent(
                targetState = uiState,
                label = "TournamentStateTransition"
            ) { state ->
                when {
                    state.isLoading -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                    }
                    state.error != null -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = state.error ?: "Unknown error",
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
                        ) {

                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    shape = MaterialTheme.shapes.extraLarge,
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1440))
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(20.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.EmojiEvents,
                                                contentDescription = "Tournaments",
                                                tint = Color(0xFFFFD54F)
                                            )
                                            Spacer(Modifier.width(12.dp))
                                            Text(
                                                text = "Tournament Lobby",
                                                style = MaterialTheme.typography.headlineSmall,
                                                color = Color.White
                                            )
                                        }

                                        Spacer(Modifier.height(8.dp))

                                        Text(
                                            text = "Discover ongoing competitions, join a tournament, or review standings.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color(0xFFB0B0C3)
                                        )
                                    }
                                }

                                Spacer(Modifier.height(12.dp))
                                HorizontalDivider()
                            }

                            items(
                                items = uiState.tournaments,
                                key = { it.id }
                            ) { tournament ->

                                TournamentItem(
                                    tournament = tournament,
                                    onJoinClick = { viewModel.joinTournament(it) },
                                    onStandingsClick = onTournamentClick,
                                    onLeaveClick = { viewModel.leaveTournament(it) },
                                    isRegistered = uiState.standings.any {
                                        currentUserId != null && it.playerId == currentUserId
                                        },
                                    )
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TournamentScreenCompact(
    viewModel: TournamentViewModel = hiltViewModel(),
    onTournamentClick: (Long) -> Unit,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.CenterHorizontally)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else if (uiState.error != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = uiState.error ?: "Unknown error",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {

                item {
                    TournamentTableHeader()
                    HorizontalDivider()
                }

                items(
                    items = uiState.tournaments,
                    key = { it.id }
                ) { tournament ->

                    TournamentRow(
                        tournament = tournament,
                        onJoinClick = { viewModel.joinTournament(it) },
                        onStandingsClick = onTournamentClick
                    )

                    HorizontalDivider()
                }
            }
        }
    }
}