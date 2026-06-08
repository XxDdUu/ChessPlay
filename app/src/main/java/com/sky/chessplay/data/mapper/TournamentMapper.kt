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
