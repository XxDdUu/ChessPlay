package com.sky.chessplay.domain.repository

import com.sky.chessplay.data.remote.dto.response.FriendResponse

interface FriendRepository {

    suspend fun getFriends(userId: Long): List<FriendResponse>

    suspend fun getPendingRequests(userId: Long): List<FriendResponse>

    suspend fun sendFriendRequest(
        senderId: Long,
        receiverId: Long
    ): String

    suspend fun acceptFriendRequest(
        user1: Long,
        user2: Long
    ): String
    suspend fun removeFriend(
        user1: Long,
        user2: Long
    ): String

    suspend fun searchNewFriends(
        userId: Long,
        query: String
    ): List<com.sky.chessplay.data.remote.dto.response.UserSearchResponse>
}