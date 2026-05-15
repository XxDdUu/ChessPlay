package com.sky.chessplay.domain.model.chess

enum class PieceColor {
    WHITE,
    BLACK;

    fun opposite(): PieceColor {
        return if (this == WHITE) BLACK else WHITE
    }
}
