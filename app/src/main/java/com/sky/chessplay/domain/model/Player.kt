package com.sky.chessplay.domain.model

import com.sky.chessplay.domain.model.auth.User

data class Player(
    val user: User,
    val color: PieceColor,   // WHITE / BLACK
    val isReady: Boolean = false
)
