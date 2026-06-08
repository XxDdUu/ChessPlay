package com.sky.chessplay.domain.usecases

import com.sky.chessplay.domain.repository.TournamentRepository
import javax.inject.Inject

class GetTournamentPairingsUseCase @Inject constructor(
    private val repository: TournamentRepository
) {
    suspend operator fun invoke(
        tournamentId: Long,
        roundId: Long
    ) = repository.getPairings(tournamentId, roundId)
}
