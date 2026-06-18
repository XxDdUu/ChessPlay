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
    val draws: Int,

    @SerializedName("winRate")
    val winRate: Double?,

    @SerializedName("goldMedals")
    val goldMedals: Int?,

    @SerializedName("silverMedals")
    val silverMedals: Int?,

    @SerializedName("bronzeMedals")
    val bronzeMedals: Int?,

    @SerializedName("tournamentHistory")
    val tournamentHistory: List<UserTournamentResponse>?
)

data class UserTournamentResponse(
    @SerializedName("tournamentId")
    val tournamentId: Int,

    @SerializedName("tournamentName")
    val tournamentName: String,

    @SerializedName("startTime")
    val startTime: String?,

    val status: String,
    val rank: Int,
    val medal: String?,
    val score: Double
)

