package com.sky.chessplay.domain.socket

import com.sky.chessplay.domain.model.chess.Move
import kotlinx.coroutines.flow.SharedFlow

interface ChessSocket {
    fun connect(token: String)
    fun disconnect()
    fun sendMove(move: Move, activeGameId: String?)
    fun sendReady(gameId: String?)
    fun observeEvents(listener: (SocketEvent) -> Unit)
    fun createRoom(matchType: String)
    fun joinRoom(code: String)
    fun sendChatMessage(gameId: String?, message: String)
    val events: SharedFlow<MatchEvent>
    val socketEvents: SharedFlow<SocketEvent>
}
