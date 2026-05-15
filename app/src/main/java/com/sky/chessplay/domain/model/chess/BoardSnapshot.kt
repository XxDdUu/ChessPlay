package com.sky.chessplay.domain.model.chess


data class BoardSnapshot(
    val piecesByPosition: Board,
    val sideToPlay: Side,
    val move: Move? = null,
)