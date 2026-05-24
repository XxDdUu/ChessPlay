package com.sky.chessplay.domain.state

import com.sky.chessplay.data.remote.dto.response.FriendResponse

sealed class FriendState {

    data object Idle : FriendState()

    data object Loading : FriendState()

    data class FriendsLoaded(
        val friends: List<FriendResponse>
    ) : FriendState()

    data class PendingLoaded(
        val pendingRequests: List<FriendResponse>
    ) : FriendState()
    data class FriendRequestSent(
        val message: String
    ) : FriendState()
    data class Success(
        val message: String
    ) : FriendState()

    data class Error(
        val message: String
    ) : FriendState()
}