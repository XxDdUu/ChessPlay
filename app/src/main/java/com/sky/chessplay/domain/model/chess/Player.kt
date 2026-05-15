package com.sky.chessplay.domain.model.chess

import com.sky.chessplay.domain.model.auth.User

data class Player(
    val user: User,
    val color: PieceColor,
    val isReady: Boolean = false
)
