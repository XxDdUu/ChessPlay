package com.sky.chessplay.data.socket

import android.os.Handler
import android.os.Looper
import com.sky.chessplay.domain.model.Move
import com.sky.chessplay.domain.model.toUci
import com.sky.chessplay.domain.socket.ChessSocket
import com.sky.chessplay.domain.socket.SocketEvent
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject

class ChessSocketClient : ChessSocket {

    private var webSocket: WebSocket? = null
    private val listeners = mutableListOf<(SocketEvent) -> Unit>()

    private var currentGameId: String? = null
    private var lastToken: String? = null
    private var reconnectAttempts = 0

    override fun sendReady() {
        val json = JSONObject()
            .put("type", "READY")

        webSocket?.send(json.toString())
    }

    override fun connect(gameId: String, token: String) {
        this.currentGameId = gameId
        this.lastToken = token
        reconnectAttempts = 0

        val request = Request.Builder()
            .url("ws://10.0.2.2:8080/ws?token=$token&gameId=$gameId")
            .build()

        webSocket = OkHttpClient().newWebSocket(request, socketListener)
    }

    override fun observeEvents(listener: (SocketEvent) -> Unit) {
        listeners += listener
    }

    override fun sendMove(move: Move) {
        val json = JSONObject()
            .put("type", "MOVE")
            .put("move", move.toUci())

        webSocket?.send(json.toString())
    }
    override fun disconnect() {
        webSocket?.close(1000, "Client disconnect")
        webSocket = null
    }

    private val socketListener = object : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            listeners.forEach { it(SocketEvent.Connected) }
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            val json = JSONObject(text)
            val type = json.getString("type")

            val event = when (type) {
                "OPPONENT_MOVE" -> SocketEvent.GameUpdate(
                    fen = json.getString("fen"),
                    move = json.getString("move")
                )
                else -> null
            }

            event?.let { e -> listeners.forEach { it(e) } }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            listeners.forEach { it(SocketEvent.Error("Connection error")) }

            if (reconnectAttempts < 5) {
                reconnectAttempts++

                Handler(Looper.getMainLooper()).postDelayed({
                    currentGameId?.let { gId ->
                        lastToken?.let { tk ->
                            connect(gId, tk)
                        }
                    }
                }, 2000L * reconnectAttempts)
            }
        }
    }
}
