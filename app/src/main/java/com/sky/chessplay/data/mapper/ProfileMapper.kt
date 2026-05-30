package com.sky.chessplay.data.mapper

import com.sky.chessplay.data.remote.dto.response.GameHistoryResponse
import com.sky.chessplay.data.remote.dto.response.UserProfileResponse
import com.sky.chessplay.domain.model.profile.GameHistoryItem
import com.sky.chessplay.domain.model.profile.UserStats

object ProfileMapper {
    fun UserProfileResponse.toDomain(): UserStats {
        return UserStats(
            rating = rating,
            gamesPlayed = gamesPlayed,
            wins = wins,
            losses = losses,
            draws = draws
        )
    }

    fun GameHistoryResponse.toDomain(): GameHistoryItem {
        return GameHistoryItem(
            gameId = gameId.toString(),
            opponentName = opponentName ?: "Unknown",
            myColor = myColor,
            result = result,
            pgn = pgn,
            playedAt = playedAt
        )
    }
}
