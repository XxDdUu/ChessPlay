package com.sky.chessplay.domain.state

import com.sky.chessplay.domain.model.tournament.Standing
import com.sky.chessplay.domain.model.tournament.Tournament
import com.sky.chessplay.domain.model.tournament.TournamentPairing
import com.sky.chessplay.domain.model.tournament.TournamentRound

data class TournamentDetailUiState(
    val isLoading: Boolean = false,
    val isPairingsLoading: Boolean = false,
    val tournament: Tournament? = null,
    val standings: List<Standing> = emptyList(),
    val rounds: List<TournamentRound> = emptyList(),
    val selectedRoundId: Long? = null,
    val pairings: List<TournamentPairing> = emptyList(),
    val myPairing: com.sky.chessplay.domain.model.tournament.MyPairing? = null,
    val roundsError: String? = null,
    val error: String? = null
)
