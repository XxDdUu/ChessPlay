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
        startTime = startTime ?: "",
        status = status.toTournamentStatus(),
        createdById = creatorId?.toLong() ?: 0L,
        createdByName = creatorName ?: ""
    )
}
fun String?.toTournamentStatus(): TournamentStatus {
    return try {
        TournamentStatus.valueOf(this ?: "")
    } catch (_: Exception) {
        TournamentStatus.UNKNOWN
    }
}