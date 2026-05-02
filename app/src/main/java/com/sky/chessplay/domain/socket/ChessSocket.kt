package com.sky.chessplay.domain.socket

import com.sky.chessplay.domain.model.Move

interface ChessSocket {
    fun connect(token: String)
    fun disconnect()
    fun sendMove(move: Move, activeGameId: String?)
    fun sendReady(gameId: String?)
    fun observeEvents(listener: (SocketEvent) -> Unit)
}
