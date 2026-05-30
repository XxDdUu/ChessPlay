package com.sky.chessplay.data.remote.api

import com.sky.chessplay.data.remote.dto.response.GameHistoryResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GameApi {

    @GET("api/game/history")
    suspend fun getHistory(
        @Query("userId") userId: Int,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): List<GameHistoryResponse>

    @GET("api/game/active")
    suspend fun getActiveGame(
        @Query("userId") userId: Int
    ): String?
}