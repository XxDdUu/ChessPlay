package com.sky.chessplay.domain.socket

import com.sky.chessplay.domain.model.Move

interface ChessSocket {
    fun connect(gameId: String, token: String)
    fun disconnect()
    fun sendMove(move: Move)
    fun sendReady()
    fun observeEvents(listener: (SocketEvent) -> Unit)
}
