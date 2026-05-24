package com.sky.chessplay.ui.presentation.chess.online_play

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sky.chessplay.domain.model.chat.ChatMessage
import com.sky.chessplay.domain.socket.ChessSocket
import com.sky.chessplay.domain.socket.SocketEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val socket: ChessSocket
) : ViewModel() {
    private var myUserId: Long? = null
    var chatMessages by mutableStateOf<List<ChatMessage>>(emptyList())
        private set

    var input by mutableStateOf("")
        private set
    var unreadCount by mutableIntStateOf(0)
        private set
    var isChatOpened by mutableStateOf(false)

    fun setCurrentUser(userId: Long) {
        myUserId = userId

        chatMessages = chatMessages.map {
            it.copy(
                isMine = it.senderId == userId
            )
        }
    }
    init {


        viewModelScope.launch {

            socket.socketEvents.collect { event ->

                when(event) {

                    is SocketEvent.ChatMessageReceived -> {

                        chatMessages = chatMessages + ChatMessage(
                            senderId = event.senderId,
                            senderName = event.senderName,
                            message = event.message,
                            isMine = event.senderId == myUserId
                        )

                        val isMyMessage = event.senderId == myUserId

                        if (!isMyMessage && !isChatOpened) {
                            unreadCount++
                        }
                    }
                    is SocketEvent.GameInit -> {
                        if(event.chatHistory.isNotEmpty()) {
                            chatMessages = event.chatHistory
                        }
                    }


                    else -> Unit
                }
            }
        }
    }
    fun markAsRead() {
        unreadCount = 0
    }
    fun updateInput(text: String) {
        input = text
    }


    fun sendMessage(
        gameId: String?,
        myName: String
    ) {

        if (input.isBlank()) return

        val msg = input.trim()

        chatMessages = chatMessages + ChatMessage(
            senderName = myName,
            message = msg,
            isMine = true,
            senderId = myUserId
        )

        socket.sendChatMessage(gameId, msg)

        input = ""
    }
}