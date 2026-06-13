package com.sky.chessplay.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class StandingResponse (
    @SerializedName("playerId")
    val playerId: Long?,
    @SerializedName("userId")
    val userId: Long?,
    @SerializedName("username")
    val username: String?,
    @SerializedName("initialRating")
    val initialRating: Int?,
    @SerializedName("currentScore")
    val currentScore: Double?,
    @SerializedName("buchholz")
    val buchholz: Double?,
    @SerializedName("sonnebornBerger")
    val sonnebornBerger: Double?,
    @SerializedName("rank")
    val rank: Int?
)
