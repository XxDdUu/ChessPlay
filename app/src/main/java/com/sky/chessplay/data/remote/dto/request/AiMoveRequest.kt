package com.sky.chessplay.data.remote.dto.request

data class AiMoveRequest(
    val gameId: String,
    val move: String
)
