package com.sky.chessplay.domain.socket

import com.sky.chessplay.domain.model.chat.ChatMessage
import com.sky.chessplay.domain.model.chess.Side

sealed class LocalEvent {

    data class GameInit(
        val gameId: String,
        val side: Side,
        val fen: String,
        val opponentId: Long = 0,
        val opponentName: String? = "Computer",
        val opponentRating: Int? = null,
        val timeWhite: Int = 600,
        val timeBlack: Int = 600,
        val history: List<String>? = null,
        val chatHistory: List<ChatMessage> = emptyList(),
    ) : LocalEvent()

    data class Move(
        val move: String,
        val fen: String?,
        val timeRemaining: Int? = null
    ) : LocalEvent()

    data class GameOver(
        val result: String,
        val reason: String
    ) : LocalEvent()

    data class Error(val message: String) : LocalEvent()

    data class Opponent(
        val name: String,
        val rating: Int
    )

    enum class GameResult {
        WHITE_WIN, BLACK_WIN, DRAW
    }
}
