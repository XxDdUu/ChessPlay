package com.sky.chessplay.data.mapper

import com.sky.chessplay.data.remote.dto.response.TournamentResponse
import com.sky.chessplay.domain.model.tournament.Tournament
import com.sky.chessplay.domain.state.TournamentStatus

fun TournamentResponse.toDomain(): Tournament {
    return Tournament(
        id = id ?: 0L,
        name = name ?: "",
        description = description ?: "",
        totalRounds = totalRounds ?: 0,
        timeControl = timeControl ?: "",
        registrationStart = registrationStart ?: "",
        registrationEnd = registrationEnd ?: "",
        startTime = startTime ?: "",
        status = status.toTournamentStatus(),
        createdById = creatorId?.toLong() ?: 0L,
        createdByName = creatorName ?: ""
    )
}
fun String?.toTournamentStatus(): TournamentStatus {
    val normalized = when (this) {
        "IN_PROGRESS" -> "ONGOING"
        else -> this
    }

    return try {
        TournamentStatus.valueOf(normalized ?: "")
    } catch (_: Exception) {
        TournamentStatus.UNKNOWN
    }
}

fun com.sky.chessplay.data.remote.dto.response.MyPairingResponse.toDomain(): com.sky.chessplay.domain.model.tournament.MyPairing {
    return com.sky.chessplay.domain.model.tournament.MyPairing(
        pairingId = pairingId ?: 0L,
        roundNumber = roundNumber ?: 0,
        opponentName = opponentName ?: "BYE",
        opponentRating = opponentRating ?: 1200,
        myColor = myColor ?: "WHITE",
        isBye = isBye ?: false,
        lobbyTimeLimitSeconds = lobbyTimeLimitSeconds ?: 300,
        lobbyTimeLeftSeconds = lobbyTimeLeftSeconds ?: 0L,
        iAmReady = iAmReady ?: false,
        opponentReady = opponentReady ?: false,
        result = result,
        gameId = gameId,
        inBreak = inBreak ?: false,
        breakTimeLeftSeconds = breakTimeLeftSeconds ?: 0L
    )
}
