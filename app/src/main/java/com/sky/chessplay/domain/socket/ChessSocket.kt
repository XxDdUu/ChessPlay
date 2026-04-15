package com.sky.chessplay.domain.socket

import com.sky.chessplay.domain.model.Move
import okhttp3.Response
import okhttp3.WebSocket

interface ChessSocket {
    fun connect(gameId: String, token: String)
    fun disconnect()
    fun onOpen(webSocket: WebSocket, response: Response)
    fun onClosed(webSocket: WebSocket, code: Int, reason: String)
    fun sendMove(gameId: String, move: Move)
    fun sendReady(gameId: String)
    fun sendDrawOffer(gameId: String)
    fun sendDrawResponse(gameId: String, accepted: Boolean)
    fun sendResign(gameId: String)

    fun observeEvents(listener: (SocketEvent) -> Unit)
}
