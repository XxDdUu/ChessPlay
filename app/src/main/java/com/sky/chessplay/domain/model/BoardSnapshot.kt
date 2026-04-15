package com.sky.chessplay.domain.model


data class BoardSnapshot(
    val piecesByPosition: Board,
    val sideToPlay: Side,
    val move: Move? = null,
)