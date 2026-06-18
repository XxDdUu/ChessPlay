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
            draws = draws,
            winRate = winRate ?: 0.0,
            goldMedals = goldMedals ?: 0,
            silverMedals = silverMedals ?: 0,
            bronzeMedals = bronzeMedals ?: 0,
            tournamentHistory = tournamentHistory?.map { t ->
                com.sky.chessplay.domain.model.profile.UserTournament(
                    tournamentId = t.tournamentId,
                    tournamentName = t.tournamentName,
                    startTime = t.startTime,
                    status = t.status,
                    rank = t.rank,
                    medal = t.medal,
                    score = t.score
                )
            } ?: emptyList()
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
