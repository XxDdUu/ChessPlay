package com.sky.chessplay.data.socket

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.sky.chessplay.domain.model.Move
import com.sky.chessplay.domain.model.toUci
import com.sky.chessplay.domain.socket.ChessSocket
import com.sky.chessplay.domain.socket.MatchEvent
import com.sky.chessplay.domain.socket.SocketEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import javax.inject.Inject

class ChessSocketClient @Inject constructor() : ChessSocket {

    private val _events = MutableSharedFlow<MatchEvent>()
    val events = _events.asSharedFlow()
    private var webSocket: WebSocket? = null
    private val listeners = mutableListOf<(SocketEvent) -> Unit>()

    private var currentGameId: String? = null
    private var lastToken: String? = null
    private var reconnectAttempts = 0

    override fun connect( token: String) {
        this.lastToken = token
        reconnectAttempts = 0

        val request = Request.Builder()
            .url("ws://10.0.2.2:8080/ws?token=$token")
            .build()

        webSocket = OkHttpClient().newWebSocket(request, socketListener)
    }

    override fun sendReady() {
        val json = JSONObject()
            .put("type", "READY")

        webSocket?.send(json.toString())
    }

    override fun sendMove(move: Move) {
        val json = JSONObject()
            .put("type", "MOVE")
            .put("move", move.toUci())

        webSocket?.send(json.toString())
    }

    override fun observeEvents(listener: (SocketEvent) -> Unit) {
        listeners += listener
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
            try {
                Log.d("SOCKET_RAW", text) // 🔥

                val json = JSONObject(text)
                val type = json.getString("type")

                val socketEvent = when (type) {

                    "GAME_START", "RECONNECT_GAME" -> {
                        SocketEvent.GameInit(
                            gameId = json.getString("gameId"),
                            side = json.getString("side"),
                            fen = json.getString("fen"),
                            opponentId = json.getLong("opponentId"),
                            opponentName = json.optString("opponentName"),
                            opponentRating = json.optInt("opponentRating"),
                            history = null
                        )
                    }

                    "OPPONENT_MOVE" -> {
                        SocketEvent.Move(
                            move = json.getString("move"),
                            fen = json.getString("fen")
                        )
                    }

                    "GAME_OVER" -> {
                        SocketEvent.GameOver(
                            result = json.getString("result"),
                            reason = json.getString("reason")
                        )
                    }

                    else -> null
                }

                socketEvent?.let { e ->
                    listeners.forEach { it(e) }
                }

                val matchEvent = when (type) {

                    "PREPARE_GAME" -> MatchEvent.PrepareGame(
                        gameId = json.getString("gameId"),
                        opponentId = json.getLong("opponentId"),
                        opponentName = json.getString("opponentName"),
                        opponentCountry = json.optString("opponentCountry"),
                        opponentRating = json.optInt("opponentRating"),
                        timeout = json.optInt("timeout", 10)
                    )

                    "MATCH_CANCELLED" -> MatchEvent.MatchCancelled(
                        reason = json.optString("reason", "Cancelled")
                    )

                    "GAME_START" -> MatchEvent.GameStart(
                        gameId = json.getString("gameId"),
                        side = json.getString("side"),
                        fen = json.getString("fen"),
                        opponentName = json.optString("opponentName"),
                        opponentRating = json.optInt("opponentRating")
                    )

                    "RECONNECT_GAME" -> MatchEvent.ReconnectGame(
                        gameId = json.getString("gameId"),
                        side = json.getString("side"),
                        fen = json.getString("fen"),
                        opponentId = json.getLong("opponentId"),
                        opponentName = json.getString("opponentName"),
                        opponentRating = json.optInt("opponentRating")
                    )

                    "ERROR" -> MatchEvent.Error(
                        message = json.optString("message", "Unknown error")
                    )

                    else -> null
                }

                matchEvent?.let { event ->
                    Log.d("SOCKET_MATCH_EVENT", event.toString())
                    CoroutineScope(Dispatchers.IO).launch {
                        _events.emit(event)
                        Log.d("SOCKET_EMIT", "Emitted: $event")
                    }
                }

            } catch (e: Exception) {
                CoroutineScope(Dispatchers.IO).launch {
                    _events.emit(MatchEvent.Error("Parse error: ${e.message}"))
                }
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            listeners.forEach { it(SocketEvent.Error("Connection error")) }

            if (reconnectAttempts < 5) {
                reconnectAttempts++

                Handler(Looper.getMainLooper()).postDelayed({
                        lastToken?.let { tk ->
                            connect( tk)
                        }
                }, 2000L * reconnectAttempts)
            }
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            listeners.forEach { it(SocketEvent.Disconnected) }
        }
    }
}
