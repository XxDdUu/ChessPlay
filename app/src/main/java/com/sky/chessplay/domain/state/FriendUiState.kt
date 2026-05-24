package com.sky.chessplay.domain.state

data class FriendUiState(

    val isSendingRequest: Boolean = false,

    val friendRequestSent: Boolean = false,

    val statusMessage: String? = null
)