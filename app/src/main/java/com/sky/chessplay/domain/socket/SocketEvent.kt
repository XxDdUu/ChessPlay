package com.sky.chessplay.domain.socket

import com.sky.chessplay.domain.model.Side

sealed class SocketEvent {

    object Connected : SocketEvent()
    object Disconnected : SocketEvent()

    data class GameInit(
        val gameId: String,
        val side: Side,
        val fen: String,
        val opponentId: Long,
        val opponentName: String?,
        val opponentRating: Int?,
        val history: List<String>? = null
    ) : SocketEvent()

    data class Move(
        val move: String,
        val fen: String?
    ) : SocketEvent()

    data class GameOver(
        val result: String,
        val reason: String
    ) : SocketEvent()

    data class DrawOffered(val gameId: String) : SocketEvent()
    object DrawRejected : SocketEvent()

    data class Error(val message: String) : SocketEvent()
    data class Opponent(
        val name: String,
        val rating: Int
    )
    enum class GameStatus {
        WAITING,
        PLAYING,
        FINISHED
    }
    enum class GameResult {
        WHITE_WIN, BLACK_WIN, DRAW
    }
}
