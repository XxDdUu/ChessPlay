package com.sky.chessplay.data.repository

import com.sky.chessplay.data.remote.api.FriendApi
import com.sky.chessplay.data.remote.dto.response.FriendResponse
import com.sky.chessplay.domain.repository.FriendRepository
import javax.inject.Inject

class FriendRepositoryImpl @Inject constructor(
    private val api: FriendApi
) : FriendRepository {

    override suspend fun getFriends(userId: Long): List<FriendResponse> {
        val response = api.getFriends(userId)

        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw Exception("Failed to load friends")
        }
    }

    override suspend fun getPendingRequests(userId: Long): List<FriendResponse> {
        val response = api.getPending(userId)

        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw Exception("Failed to load pending requests")
        }
    }

    override suspend fun sendFriendRequest(
        senderId: Long,
        receiverId: Long
    ): String {
        val response = api.sendRequest(senderId, receiverId)
        if (response.isSuccessful) {
            return response.body() ?: "Success"
        } else {
            throw Exception("Failed to send friend request")
        }
    }

    override suspend fun acceptFriendRequest(
        user1: Long,
        user2: Long
    ): String {
        val response = api.acceptRequest(user1, user2)
        if (response.isSuccessful) {
            return response.body() ?: "Success"
        } else {
            throw Exception("Failed to accept friend request")
        }
    }
    override suspend fun removeFriend(
        user1: Long,
        user2: Long
    ): String {
        val response = api.removeFriend(
            user1,
            user2
        )
        if (response.isSuccessful) {
            return response.body() ?: "Success"
        } else {
            throw Exception("Failed to remove friend")
        }
    }

    override suspend fun searchNewFriends(
        userId: Long,
        query: String
    ): List<com.sky.chessplay.data.remote.dto.response.UserSearchResponse> {
        val response = api.searchNewFriends(userId, query)
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw Exception("Failed to search new friends")
        }
    }
}