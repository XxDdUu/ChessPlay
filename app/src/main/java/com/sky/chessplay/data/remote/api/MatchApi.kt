package com.sky.chessplay.data.remote.api

import retrofit2.http.POST
import retrofit2.http.Query

interface MatchApi {
    @POST("api/matchmaking/join")
    suspend fun joinMatch(
        @Query("userId") userId: Long,
        @Query("matchType") matchType: String
    )

    @POST("api/matchmaking/leave")
    suspend fun leaveMatch(
        @Query("userId") userId: Long
    )
}
