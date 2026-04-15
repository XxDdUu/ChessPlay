package com.sky.chessplay.domain.socket

sealed class SocketEvent {
    object Connected : SocketEvent()
    object Disconnected : SocketEvent()
    data class GameStart(
        val gameId: String,
        val side: String,
        val opponent: Int
    ) : SocketEvent()

    data class GameUpdate(
        val fen: String,
        val move: String
    ) : SocketEvent()

    data class GameOver(
        val result: String,
        val reason: String
    ) : SocketEvent()

    data class DrawOffered(val gameId: String) : SocketEvent()
    object DrawRejected : SocketEvent()

    data class Reconnect(
        val gameId: String,
        val fen: String,
        val side: String,
        val opponent: Int,
        val history: List<String?>
    ) : SocketEvent()
    data class Error(val message: String) : SocketEvent()
}
