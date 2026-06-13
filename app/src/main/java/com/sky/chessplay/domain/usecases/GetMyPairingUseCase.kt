package com.sky.chessplay.domain.usecases

import com.sky.chessplay.domain.model.tournament.MyPairing
import com.sky.chessplay.domain.repository.TournamentRepository
import javax.inject.Inject

class GetMyPairingUseCase @Inject constructor(
    private val repository: TournamentRepository
) {
    suspend operator fun invoke(tournamentId: Long): Result<MyPairing?> =
        repository.getMyPairing(tournamentId)
}
