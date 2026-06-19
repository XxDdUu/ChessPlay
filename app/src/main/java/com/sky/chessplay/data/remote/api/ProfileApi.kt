package com.sky.chessplay.data.remote.api

import com.sky.chessplay.data.remote.dto.response.LeaderboardResponse
import com.sky.chessplay.data.remote.dto.response.UserProfileResponse
import retrofit2.http.GET

interface ProfileApi {

    @GET("api/user/me")
    suspend fun getProfile(): UserProfileResponse

    @GET("api/user/leaderboard")
    suspend fun getLeaderboard(): List<LeaderboardResponse>

    @GET("api/user/{userId}/stats")
    suspend fun getUserStats(
        @retrofit2.http.Path("userId") userId: Long
    ): UserProfileResponse
}
