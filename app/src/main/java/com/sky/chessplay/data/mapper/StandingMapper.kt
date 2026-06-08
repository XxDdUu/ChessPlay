package com.sky.chessplay.data.mapper

import com.sky.chessplay.data.remote.dto.response.StandingResponse
import com.sky.chessplay.domain.model.tournament.Standing


fun StandingResponse.toDomain(): Standing {
    return Standing(
        playerId = playerId ?: 0L,
        username = username ?: "",
        currentScore = currentScore ?: 0.0,
        buchholz = buchholz ?: 0.0,
        sonnebornBerger = sonnebornBerger ?: 0.0,
        rank = rank ?: 0
    )
}