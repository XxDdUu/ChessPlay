package com.sky.chessplay.data.remote.dto.request

data class AiStartRequest(
    val aiModel: String,
    val difficulty: Int,
    val playerColor: String
)
