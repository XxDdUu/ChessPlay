package com.sky.chessplay.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class GameHistoryResponse(
    @SerializedName("gameId")
    val gameId: Int,

    @SerializedName("opponentId")
    val opponentId: Int?,

    @SerializedName("opponentName")
    val opponentName: String?,

    @SerializedName("myColor")
    val myColor: String,

    @SerializedName("result")
    val result: String,

    @SerializedName("pgn")
    val pgn: String?,

    @SerializedName("playedAt")
    val playedAt: String
)
