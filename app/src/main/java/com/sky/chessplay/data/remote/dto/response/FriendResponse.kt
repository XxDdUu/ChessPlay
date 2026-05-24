package com.sky.chessplay.data.remote.dto.response

data class FriendResponse(
    val userId: Long,
    val username: String,
    val status: String, // ONLINE / OFFLINE
    val rating: Int
)