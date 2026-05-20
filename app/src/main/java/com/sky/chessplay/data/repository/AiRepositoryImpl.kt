package com.sky.chessplay.data.repository

import com.sky.chessplay.data.remote.api.AiApi
import com.sky.chessplay.data.remote.dto.request.AiMoveRequest
import com.sky.chessplay.data.remote.dto.request.AiResignRequest
import com.sky.chessplay.data.remote.dto.request.AiStartRequest
import com.sky.chessplay.data.remote.dto.response.AiGameResponse
import com.sky.chessplay.data.remote.dto.response.AiModelsResponse
import com.sky.chessplay.domain.repository.AiRepository
import javax.inject.Inject

class AiRepositoryImpl @Inject constructor(
    private val api: AiApi
) : AiRepository {

    override suspend fun getAvailableModels(): AiModelsResponse {
        val response = api.getAvailableModels()
        if (response.isSuccessful) {
            return response.body() ?: AiModelsResponse(emptyList(), "best_model")
        } else {
            throw Exception("Failed to load AI models")
        }
    }

    override suspend fun startGame(
        aiModel: String,
        difficulty: Int,
        playerColor: String
    ): AiGameResponse {
        val response = api.startGame(AiStartRequest(aiModel, difficulty, playerColor))
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Failed to start AI game: empty response body")
        } else {
            throw Exception("Failed to start AI game: ${response.message()}")
        }
    }

    override suspend fun makeMove(gameId: String, move: String): AiGameResponse {
        val response = api.makeMove(AiMoveRequest(gameId, move))
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Failed to make AI move: empty response body")
        } else {
            throw Exception("Failed to make AI move: ${response.message()}")
        }
    }

    override suspend fun resignGame(gameId: String): AiGameResponse {
        val response = api.resignGame(AiResignRequest(gameId))
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Failed to resign AI game: empty response body")
        } else {
            throw Exception("Failed to resign AI game: ${response.message()}")
        }
    }

    override suspend fun checkActiveGame(): AiGameResponse? {
        val response = api.checkActiveGame()
        if (response.code() == 204) {
            return null
        }
        if (response.isSuccessful) {
            return response.body()
        } else {
            throw Exception("Failed to check active AI game: ${response.message()}")
        }
    }

    override suspend fun getGameDetails(gameId: String): AiGameResponse {
        val response = api.getGameDetails(gameId)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Failed to get AI game details: empty response body")
        } else {
            throw Exception("Failed to get AI game details: ${response.message()}")
        }
    }
}
