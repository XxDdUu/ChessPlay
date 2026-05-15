package com.sky.chessplay.domain.model.chess

enum class Side {
    WHITE, BLACK;

    val opposite: Side
        get() = when (this) {
            WHITE -> BLACK
            BLACK -> WHITE
        }
}
