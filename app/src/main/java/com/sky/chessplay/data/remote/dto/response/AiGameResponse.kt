package com.sky.chessplay.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class AiGameResponse(
    val gameId: String,
    val fen: String,
    val playerColor: String,
    val difficulty: Int,
    val aiModel: String,
    @SerializedName("isGameOver")
    val isGameOver: Boolean,
    val result: String?,
    val termination: String?,
    val aiFirstMove: String?,
    val history: List<String>?
)
