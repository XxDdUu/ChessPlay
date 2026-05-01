package com.sky.chessplay.domain.state

enum class MatchState {
    INITIALIZING,
    SEARCHING,
    FOUND,
    COUNTDOWN,
    PLAYING,
    OVER,
    CANCELLED
}
