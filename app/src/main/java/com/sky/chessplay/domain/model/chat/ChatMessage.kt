package com.sky.chessplay.domain.model.chat

data class ChatMessage(
    val senderId: Long?,
    val senderName: String,
    val message: String,
    val isMine: Boolean
)
