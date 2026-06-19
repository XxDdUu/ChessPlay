package com.sky.chessplay.domain.state

import com.sky.chessplay.data.remote.dto.response.UserSearchResponse

data class FriendUiState(
    val isSendingRequest: Boolean = false,
    val friendRequestSent: Boolean = false,
    val statusMessage: String? = null,
    val searchResults: List<UserSearchResponse> = emptyList(),
    val searchQuery: String = ""
)