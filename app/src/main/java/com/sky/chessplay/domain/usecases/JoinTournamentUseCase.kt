package com.sky.chessplay.domain.usecases

import com.sky.chessplay.domain.repository.TournamentRepository
import javax.inject.Inject

class JoinTournamentUseCase @Inject constructor(
    private val repository: TournamentRepository
) {
    suspend operator fun invoke(
        tournamentId: Long
    ) = repository.joinTournament(tournamentId)
}