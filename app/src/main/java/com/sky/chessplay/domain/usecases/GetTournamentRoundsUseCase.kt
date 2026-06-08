package com.sky.chessplay.domain.usecases

import com.sky.chessplay.domain.repository.TournamentRepository
import javax.inject.Inject

class GetTournamentRoundsUseCase @Inject constructor(
    private val repository: TournamentRepository
) {
    suspend operator fun invoke(tournamentId: Long) =
        repository.getRounds(tournamentId)
}
