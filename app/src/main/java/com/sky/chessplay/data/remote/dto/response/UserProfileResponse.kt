package com.sky.chessplay.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class UserProfileResponse(
    @SerializedName("userId")
    val userId: Int,

    val username: String,

    @SerializedName("countryCode")
    val countryCode: String?,

    val rating: Int,
    val gamesPlayed: Int,
    val wins: Int,
    val losses: Int,
    val draws: Int
)
