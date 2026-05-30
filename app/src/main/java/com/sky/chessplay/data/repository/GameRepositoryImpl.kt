package com.sky.chessplay.data.repository

import com.sky.chessplay.data.mapper.ProfileMapper
import com.sky.chessplay.data.remote.api.GameApi
import com.sky.chessplay.domain.model.profile.GameHistoryItem
import com.sky.chessplay.domain.repository.GameRepository
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(
    private val gameApi: GameApi
) : GameRepository {

    override suspend fun getHistory(
        userId: String,
        page: Int,
        size: Int
    ): Result<List<GameHistoryItem>> {
        return try {
            val apiUserId = userId.toIntOrNull() ?: 0
            val response = gameApi.getHistory(
                userId = apiUserId,
                page = page,
                size = size
            )
            Result.success(response.map {
                with(ProfileMapper) {
                    it.toDomain()
                }
            })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}