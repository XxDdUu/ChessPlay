package com.sky.chessplay.data.socket

import android.os.Handler
import android.os.Looper
import com.google.gson.Gson
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
    private var listener: ((SocketEvent) -> Unit)? = null
    private val gson = Gson()
    private var lastGameId: String? = null
    private var lastToken: String? = null
    override fun connect(gameId: String, token: String) {
        this.lastGameId = gameId
        this.lastToken = token
        val request = Request.Builder()
            .url("ws://10.0.2.2:8080/ws?token=$token")
            .build()
        val client = OkHttpClient()
        webSocket = client.newWebSocket(request, socketListener)
    }
    override fun onOpen(webSocket: WebSocket, response: Response) {
        listener?.invoke(SocketEvent.Connected)
    }
    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        listener?.invoke(SocketEvent.Disconnected)
    }

    override fun disconnect() {
        webSocket?.close(1000, "Client disconnect")
        webSocket = null
    }

    override fun observeEvents(listener: (SocketEvent) -> Unit) {
        this.listener = listener
    }

    // ======================
    // SEND METHODS
    // ======================

    override fun sendMove(gameId: String ,move: Move) {
        val json = JSONObject()
            .put("type", "MOVE")
            .put("gameId", gameId)
            .put("move", move.toUci())

        webSocket?.send(json.toString())
    }

    override fun sendReady(gameId: String) {
        val json = JSONObject()
            .put("type", "READY")
            .put("gameId", gameId)

        webSocket?.send(json.toString())
    }

    override fun sendDrawOffer(gameId: String) {
        val json = JSONObject()
            .put("type", "DRAW_OFFER")
            .put("gameId", gameId)

        webSocket?.send(json.toString())
    }

    override fun sendDrawResponse(gameId: String, accepted: Boolean) {
        val json = JSONObject()
            .put("type", "DRAW_RESPONSE")
            .put("gameId", gameId)
            .put("accepted", accepted)

        webSocket?.send(json.toString())
    }

    override fun sendResign(gameId: String) {
        val json = JSONObject()
            .put("type", "RESIGN")
            .put("gameId", gameId)

        webSocket?.send(json.toString())
    }

    // ======================
    // RECEIVE
    // ======================

    private val socketListener = object : WebSocketListener() {

        override fun onMessage(webSocket: WebSocket, text: String) {
            try {
                val json = JSONObject(text)
                val type = json.getString("type")

                val event = when (type) {

                    "GAME_START" -> SocketEvent.GameStart(
                        gameId = json.getString("gameId"),
                        side = json.getString("side"),
                        opponent = json.getInt("opponent")
                    )

                    "OPPONENT_MOVE" -> SocketEvent.GameUpdate(
                        fen = json.getString("fen"),
                        move = json.getString("move")
                    )

                    "GAME_OVER" -> SocketEvent.GameOver(
                        result = json.getString("result"),
                        reason = json.getString("reason")
                    )

                    "DRAW_OFFERED" -> SocketEvent.DrawOffered(
                        gameId = json.getString("gameId")
                    )

                    "DRAW_REJECTED" -> SocketEvent.DrawRejected

                    "RECONNECT_GAME" -> SocketEvent.Reconnect(
                        gameId = json.getString("gameId"),
                        fen = json.getString("fen"),
                        side = json.getString("side"),
                        opponent = json.getInt("opponent"),
                        history = json.getJSONArray("history")
                            .let { arr -> List(arr.length()) { arr.getString(it) } }
                    )

                    "ERROR" -> SocketEvent.Error(
                        message = json.getString("message")
                    )

                    else -> null
                }

                event?.let { listener?.invoke(it) }

            } catch (e: Exception) {
                listener?.invoke(SocketEvent.Error("Parse error: ${e.message}"))
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            listener?.invoke(SocketEvent.Error("Connection error"))

            Handler(Looper.getMainLooper()).postDelayed({
                lastGameId?.let { gId ->
                    lastToken?.let { tk ->
                        connect(gId, tk)
                    }
                }
            }, 2000)
        }

    }
}
