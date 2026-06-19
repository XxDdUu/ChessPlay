package com.sky.chessplay.data.remote.dto.response

import com.sky.chessplay.domain.state.FriendshipStatus

data class UserSearchResponse(
    val userId: Long,
    val username: String,
    val rating: Int,
    val friendshipStatus: FriendshipStatus
)