package com.sky.chessplay.domain.usecases

import com.sky.chessplay.domain.repository.TournamentRepository
import javax.inject.Inject

class GetTournamentsUseCase @Inject constructor(
    private val repository: TournamentRepository
) {
    suspend operator fun invoke() =
        repository.getTournaments()
}