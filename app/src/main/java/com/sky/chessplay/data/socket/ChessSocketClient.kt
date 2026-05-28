package com.sky.chessplay.data.socket

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.sky.chessplay.domain.model.chat.ChatMessage
import com.sky.chessplay.domain.model.chess.DEFAULT_FEN
import com.sky.chessplay.domain.model.chess.Move
import com.sky.chessplay.domain.model.chess.Side
import com.sky.chessplay.domain.model.chess.toUci
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
import javax.inject.Singleton

@Singleton
class ChessSocketClient @Inject constructor() : ChessSocket {

    private val _events = MutableSharedFlow<MatchEvent>()
    override val events = _events.asSharedFlow()
    private var webSocket: WebSocket? = null
    private var lastPrepareGame: MatchEvent.PrepareGame? = null
    private val _socketEvents =
        MutableSharedFlow<SocketEvent>(
            replay = 1
        )
    private var isConnected = false
    override val socketEvents =
        _socketEvents.asSharedFlow()

    private var currentGameId: String? = null
    private var lastToken: String? = null
    private var reconnectAttempts = 0
    init {
        Log.d("Socket CREATED", this.toString())
    }
    override fun connect( token: String) {
        if (isConnected) {
            return
        }
        this.lastToken = token
        reconnectAttempts = 0
        val wsScheme = if (com.sky.chessplay.BuildConfig.BASE_URL.startsWith("https://")) "wss://" else "ws://"
        val baseUrlWithoutScheme = com.sky.chessplay.BuildConfig.BASE_URL
            .removePrefix("https://")
            .removePrefix("http://")
            .removeSuffix("/")
        val wsUrl = "$wsScheme$baseUrlWithoutScheme/ws?token=$token"

        Log.d("SOCKET", "Connecting to: $wsUrl")

        val request = Request.Builder()
            .url(wsUrl)
            .build()
        webSocket = OkHttpClient().newWebSocket(request, socketListener)
    }

    override fun sendReady(gameId: String?) {
        val json = JSONObject()
            .put("type", "READY")
            .put("gameId", gameId)
        webSocket?.send(json.toString())
    }

    override fun sendMove(move: Move, activeGameId: String?) {
        if (webSocket == null) {
            return
        }
        val json = JSONObject()
            .put("type", "MOVE")
            .put("move", move.toUci().uppercase())
            .put("gameId", activeGameId)
        webSocket?.send(json.toString())
    }

    override fun sendChatMessage(gameId: String?, message: String) {
        val json = JSONObject()
            .put("type", "CHAT_MESSAGE")
            .put("gameId", gameId)
            .put("text", message)
        Log.d("message DEBUG", json.toString())
        webSocket?.send(json.toString())
    }
    override fun sendRematchOffer(gameId: String) {

        val json = JSONObject()
            .put("type", "REMATCH_OFFER")
            .put("gameId", gameId)

        webSocket?.send(json.toString())
    }

    override fun sendRematchResponse(
        gameId: String,
        accepted: Boolean
    ) {

        val json = JSONObject()
            .put("type", "REMATCH_RESPONSE")
            .put("gameId", gameId)
            .put("accepted", accepted)

        webSocket?.send(json.toString())
    }

    override fun createRoom(matchType: String) {
        val json = JSONObject()
            .put("type", "CREATE_ROOM")
            .put("matchType", matchType)
        webSocket?.send(json.toString())
    }

    override fun joinRoom(code: String) {
        val json = JSONObject()
            .put("type", "JOIN_ROOM")
            .put("code", code)
        webSocket?.send(json.toString())
    }

    override fun observeEvents(listener: (SocketEvent) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {

            socketEvents.collect { event ->

                listener(event)
            }
        }
    }

    override fun disconnect() {
        webSocket?.close(1000, "Client disconnect")
        webSocket = null
    }

    private val socketListener = object : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            isConnected = true
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d("RAW_SOCKET", text)
            try {
                val json = JSONObject(text)
                val type = json.getString("type")

                val socketEvent = when (type) {
                    "ROOM_CREATED" -> {
                        SocketEvent.RoomCreated(
                            code = json.getString("code")
                        )
                    }
                    "GAME_START" -> {

                        val prepare = lastPrepareGame

                        SocketEvent.GameInit(

                            gameId = json.getString("gameId"),

                            side = json.getString("side").toSide(),

                            fen = json.optString("fen")
                                .takeIf { it.isNotBlank() }
                                ?: DEFAULT_FEN,

                            opponentId = prepare?.opponentId ?: -1L,

                            opponentName = prepare?.opponentName,

                            opponentRating = prepare?.opponentRating,

                            history = null
                        )
                    }
                    "RECONNECT_GAME" -> {

                        val chats = mutableListOf<ChatMessage>()

                        val chatArray = json.optJSONArray("chatHistory")

                        if (chatArray != null) {

                            for (i in 0 until chatArray.length()) {

                                try {

                                    val chatObj = JSONObject(
                                        chatArray.getString(i)
                                    )

                                    val senderId = chatObj.optLong("senderId")

                                    chats.add(
                                        ChatMessage(
                                            senderId = senderId,

                                            senderName = chatObj.optString(
                                                "senderName",
                                                "Opponent"
                                            ),

                                            message = chatObj.optString("text"),

                                            isMine = false
                                        )
                                    )

                                } catch (e: Exception) {

                                    Log.e(
                                        "CHAT_PARSE",
                                        "Failed parse chat",
                                        e
                                    )
                                }
                            }
                        }

                        SocketEvent.GameInit(
                            gameId = json.getString("gameId"),

                            side = json.getString("side").toSide(),

                            fen = json.optString("fen")
                                .takeIf { it.isNotBlank() }
                                ?: DEFAULT_FEN,

                            opponentId = json.getLong("opponentId"),

                            opponentName = json.optString("opponentName"),

                            opponentRating = json.optInt("opponentRating"),

                            timeWhite = json.optInt("timeWhite", 600),

                            timeBlack = json.optInt("timeBlack", 600),

                            chatHistory = chats,

                            history = json.optJSONArray("history")
                                ?.let { arr ->
                                    List(arr.length()) { index ->
                                        arr.getString(index)
                                    }
                                },

                            )
                    }

                    "OPPONENT_MOVE" -> {
                        SocketEvent.Move(
                            move = json.getString("move"),
                            fen = json.optString("fen").takeIf { it.isNotBlank() }
                        )
                    }

                    "GAME_OVER" -> {
                        SocketEvent.GameOver(
                            result = json.getString("result"),
                            reason = json.getString("reason")
                        )
                    }
                    "CHAT_MESSAGE" -> {
                        SocketEvent.ChatMessageReceived(
                            senderId = json.optLong("senderId"),
                            senderName = json.optString("senderName", "Opponent"),
                            message = json.getString("text")
                        )
                    }

                    else -> null
                }

                socketEvent?.let { e ->
                    CoroutineScope(Dispatchers.IO).launch {
                        _socketEvents.emit(e)
                    }
                }

                val matchEvent = when (type) {
                    "PREPARE_GAME" -> {

                        MatchEvent.PrepareGame(
                            gameId = json.getString("gameId"),
                            opponentId = json.getLong("opponentId"),
                            opponentName = json.getString("opponentName"),
                            opponentCountry = json.optString("opponentCountry"),
                            opponentRating = json.optInt("opponentRating"),
                            timeout = json.optInt("timeout", 10)
                        ).also {

                            lastPrepareGame = it
                        }
                    }

                    "MATCH_CANCELLED" -> MatchEvent.MatchCancelled(
                        reason = json.optString("reason", "Cancelled")
                    )

                    "GAME_START" -> MatchEvent.GameStart(
                        gameId = json.getString("gameId"),
                        side = json.getString("side"),
                        fen = json.optString("fen", DEFAULT_FEN),
                        opponentName = json.optString("opponentName"),
                        opponentRating = json.optInt("opponentRating")
                    )

                    "RECONNECT_GAME" -> MatchEvent.ReconnectGame(
                        gameId = json.getString("gameId"),
                        side = json.getString("side"),
                        fen = json.optString("fen", DEFAULT_FEN),
                        opponentId = json.getLong("opponentId"),
                        opponentName = json.getString("opponentName"),
                        opponentRating = json.optInt("opponentRating")
                    )

                    "REMATCH_OFFERED" -> {
                        MatchEvent.RematchOffered(
                            gameId = json.getString("gameId")
                        )
                    }

                    "REMATCH_REJECTED" -> {
                        MatchEvent.RematchRejected
                    }

                    "ERROR" -> MatchEvent.Error(
                        message = json.optString("message", "Unknown error")
                    )

                    else -> null
                }

                matchEvent?.let { event ->
                    CoroutineScope(Dispatchers.IO).launch {
                        _events.emit(event as MatchEvent)
                    }
                }

            } catch (e: Exception) {
                CoroutineScope(Dispatchers.IO).launch {
                    _events.emit(MatchEvent.Error("Parse error: ${e.message}"))
                }
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e("SOCKET", "FAILED: ${t.message}", t)
            isConnected = false
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
            isConnected = false
        }
    }
}
fun String.toSide(): Side {
    return when (this.lowercase()) {
        "w", "white" -> Side.WHITE
        "b", "black" -> Side.BLACK
        else -> error("Invalid side: $this")
    }
}
