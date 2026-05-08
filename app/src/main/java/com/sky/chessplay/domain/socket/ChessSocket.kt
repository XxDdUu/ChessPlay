package com.sky.chessplay.domain.socket

import com.sky.chessplay.domain.model.Move
import kotlinx.coroutines.flow.SharedFlow

interface ChessSocket {
    fun connect(token: String)
    fun disconnect()
    fun sendMove(move: Move, activeGameId: String?)
    fun sendReady(gameId: String?)
    fun observeEvents(listener: (SocketEvent) -> Unit)
    val events: SharedFlow<MatchEvent>
    val socketEvents: SharedFlow<SocketEvent>
}
