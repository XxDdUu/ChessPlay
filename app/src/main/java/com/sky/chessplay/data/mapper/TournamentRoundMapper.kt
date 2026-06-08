package com.sky.chessplay.data.mapper

import com.sky.chessplay.data.remote.dto.response.TournamentPairingResponse
import com.sky.chessplay.data.remote.dto.response.TournamentRoundResponse
import com.sky.chessplay.domain.model.tournament.TournamentPairing
import com.sky.chessplay.domain.model.tournament.TournamentRound

fun TournamentRoundResponse.toDomain(): TournamentRound {
    return TournamentRound(
        id = roundId ?: 0L,
        number = roundNumber ?: 0,
        status = status ?: ""
    )
}

fun TournamentPairingResponse.toDomain(): TournamentPairing {
    return TournamentPairing(
        id = pairingId ?: 0L,
        whitePlayerId = whitePlayerId ?: 0L,
        whitePlayerName = whitePlayerName ?: "",
        whitePlayerRating = whitePlayerRating ?: 0,
        blackPlayerId = blackPlayerId,
        blackPlayerName = blackPlayerName ?: "",
        blackPlayerRating = blackPlayerRating,
        result = result ?: "",
        isBye = isBye ?: false,
        gameId = gameId
    )
}
