package com.sky.chessplay.domain.socket

import com.sky.chessplay.domain.model.Move

interface ChessSocket {
    fun connect(token: String)
    fun disconnect()
    fun sendMove(move: Move)
    fun sendReady()
    fun observeEvents(listener: (SocketEvent) -> Unit)
}
