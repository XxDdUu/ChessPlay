package com.sky.chessplay.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class LeaderboardResponse(
    @SerializedName("userId") val userId: Int,
    @SerializedName("username") val username: String,
    @SerializedName("rating") val rating: Int,
    @SerializedName("gamesPlayed") val gamesPlayed: Int,
    @SerializedName("wins") val wins: Int,
    @SerializedName("losses") val losses: Int,
    @SerializedName("draws") val draws: Int,
    @SerializedName("countryCode") val countryCode: String?
)
