package com.sky.chessplay.ui.presentation.tournament

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sky.chessplay.ui.component.tournament.StandingItem

@Composable
fun TournamentStandingsScreen(
    tournamentId: Long,
    viewModel: TournamentViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(tournamentId) {
        viewModel.loadStandings(tournamentId)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        when {

            uiState.isLoading -> {

                CircularProgressIndicator(
                    modifier = Modifier.align(
                        Alignment.Center
                    )
                )
            }

            uiState.error != null -> {

                Text(
                    text = uiState.error!!,
                    modifier = Modifier.align(
                        Alignment.Center
                    )
                )
            }

            else -> {

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    item {

                        Text(
                            text = "Standings",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }

                    items(
                        items = uiState.standings,
                        key = { it.playerId }
                    ) { standing ->

                        StandingItem(
                            standing = standing
                        )
                    }
                }
            }
        }
    }
}