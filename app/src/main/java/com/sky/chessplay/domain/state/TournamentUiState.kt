package com.sky.chessplay.domain.state

import com.sky.chessplay.domain.model.tournament.Standing
import com.sky.chessplay.domain.model.tournament.Tournament


data class TournamentUiState(
    val isLoading: Boolean = false,
    val tournaments: List<Tournament> = emptyList(),
    val standings: List<Standing> = emptyList(),
    val registeredTournamentIds: Set<Long> = emptySet(),
    val error: String? = null
)