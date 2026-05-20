package com.sky.chessplay.domain.repository

import com.sky.chessplay.data.remote.dto.response.AiGameResponse
import com.sky.chessplay.data.remote.dto.response.AiModelsResponse

interface AiRepository {
    suspend fun getAvailableModels(): AiModelsResponse
    suspend fun startGame(aiModel: String, difficulty: Int, playerColor: String): AiGameResponse
    suspend fun makeMove(gameId: String, move: String): AiGameResponse
    suspend fun resignGame(gameId: String): AiGameResponse
    suspend fun checkActiveGame(): AiGameResponse?
    suspend fun getGameDetails(gameId: String): AiGameResponse
}
