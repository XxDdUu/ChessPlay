package com.sky.chessplay.domain.repository

import com.sky.chessplay.domain.model.profile.GameHistoryItem

interface GameRepository {

    suspend fun getHistory(
        userId: String,
        page: Int = 0,
        size: Int = 10
    ): Result<List<GameHistoryItem>>

}