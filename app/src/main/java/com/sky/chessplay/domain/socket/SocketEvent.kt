package com.sky.chessplay.domain.socket

import com.sky.chessplay.domain.model.chat.ChatMessage
import com.sky.chessplay.domain.model.chess.Side

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
        val timeWhite: Int = 600,
        val timeBlack: Int = 600,
        val history: List<String>? = null,
        val chatHistory: List<ChatMessage> = emptyList(),
    ) : SocketEvent()

    data class Move(
        val move: String,
        val fen: String?,
        val timeRemaining: Int? = null
    ) : SocketEvent()

    data class GameOver(
        val result: String,
        val reason: String
    ) : SocketEvent()

    data class DrawOffered(val gameId: String) : SocketEvent()
    object DrawRejected : SocketEvent()
    data class DrawResponse(
        val gameId: String,
        val accepted: Boolean
    ) : SocketEvent()

    data class Error(val message: String) : SocketEvent()
    data class RematchOffered(
        val gameId: String
    ) : SocketEvent()

    object RematchRejected : SocketEvent()

    data class RematchAccepted(
        val newGameId: String
    ) : SocketEvent()
    data class Opponent(
        val name: String,
        val rating: Int
    )
    enum class GameStatus {
        WAITING,
        PLAYING,
        FINISHED
    }
    data class RoomCreated(
        val code: String
    ) : SocketEvent()
    enum class GameResult {
        WHITE_WIN, BLACK_WIN, DRAW
    }
    data class ChatMessageReceived(
        val senderId: Long,
        val senderName: String,
        val message: String,
    ) : SocketEvent()
    data class FriendPresence(
        val userId: Long,
        val online: Boolean
    ) : SocketEvent()

    data class PresenceUpdate(
        val userId: Long,
        val online: Boolean
    ) : SocketEvent()
}
