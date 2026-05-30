package com.sky.chessplay.ui.presentation.community

import FriendEvent
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sky.chessplay.domain.repository.FriendRepository
import com.sky.chessplay.domain.socket.ChessSocket
import com.sky.chessplay.domain.socket.SocketEvent
import com.sky.chessplay.domain.state.FriendState
import com.sky.chessplay.domain.state.FriendUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendViewModel @Inject constructor(
    private val repository: FriendRepository,
    private val chessSocket: ChessSocket
) : ViewModel() {

    private val _state =
        MutableStateFlow<FriendState>(FriendState.Idle)

    val state: StateFlow<FriendState> = _state
    var uiState by mutableStateOf(FriendUiState())
        private set

    init {
        viewModelScope.launch {
            chessSocket.socketEvents.collect { event ->
                if (event is SocketEvent.FriendPresence) {
                    updateFriendPresence(event)
                }
            }
        }
    }

    fun onEvent(event: FriendEvent) {

        when (event) {

            is FriendEvent.LoadFriends -> {
                loadFriends(event.userId)
            }

            is FriendEvent.LoadPendingRequests -> {
                loadPendingRequests(event.userId)
            }

            is FriendEvent.SendFriendRequest -> {
                sendFriendRequest(
                    event.senderId,
                    event.receiverId
                )
            }

            is FriendEvent.AcceptFriendRequest -> {
                acceptFriendRequest(
                    event.user1,
                    event.user2
                )
            }

            FriendEvent.ClearMessage -> {
                _state.value = FriendState.Idle
            }

            is FriendEvent.SearchFriend -> {

            }

            is FriendEvent.SendChallenge -> {
                chessSocket.inviteFriend(
                    friendId = event.friendId,
                    matchType = event.matchType
                )
            }

            is FriendEvent.ConnectFriend -> {

            }
            is FriendEvent.RemoveFriend -> {
                removeFriend(
                    event.user1,
                    event.user2
                )
            }
        }
    }

    private fun loadFriends(userId: Long) {

        viewModelScope.launch {

            _state.value = FriendState.Loading

            try {

                val friends =
                    repository.getFriends(userId)
                Log.d("LOAD FRIEND DEBUG", friends.toString())
                _state.value =
                    FriendState.FriendsLoaded(friends)

            } catch (e: Exception) {

                _state.value =
                    FriendState.Error(
                        e.message ?: "Unknown error"
                    )
            }
        }
    }

    private fun loadPendingRequests(userId: Long) {

        viewModelScope.launch {

            _state.value = FriendState.Loading

            try {

                val pending =
                    repository.getPendingRequests(userId)

                _state.value =
                    FriendState.PendingLoaded(pending)

            } catch (e: Exception) {

                _state.value =
                    FriendState.Error(
                        e.message ?: "Unknown error"
                    )
            }
        }
    }

    fun sendFriendRequest(
        senderId: Long,
        receiverId: Long
    ) {

        if (uiState.isSendingRequest) return

        viewModelScope.launch {

            uiState = uiState.copy(
                isSendingRequest = true
            )

            try {

                val message = repository.sendFriendRequest(
                    senderId,
                    receiverId
                )

                uiState = uiState.copy(
                    isSendingRequest = false,
                    friendRequestSent = true,
                    statusMessage = message
                )

            } catch (e: Exception) {

                uiState = uiState.copy(
                    isSendingRequest = false,
                    statusMessage = e.message
                        ?: "Failed to send friend request."
                )
            }
        }
    }
    private fun removeFriend(
        user1: Long,
        user2: Long
    ) {

        viewModelScope.launch {

            try {

                repository.removeFriend(
                    user1,
                    user2
                )
                onEvent(
                    FriendEvent.LoadFriends(user1)
                )

            } catch (e: Exception) {

            }
        }
    }

    private fun updateFriendPresence(presence: SocketEvent.FriendPresence) {
        val currentState = _state.value
        if (currentState is FriendState.FriendsLoaded) {
            _state.value = currentState.copy(
                friends = currentState.friends.map { friend ->
                    if (friend.userId == presence.userId) {
                        friend.copy(status = if (presence.online) "ONLINE" else "OFFLINE")
                    } else {
                        friend
                    }
                }
            )
        }
    }

    private fun acceptFriendRequest(
        user1: Long,
        user2: Long
    ) {

        viewModelScope.launch {

            _state.value = FriendState.Loading

            try {

                val message =
                    repository.acceptFriendRequest(
                        user1,
                        user2
                    )

                _state.value =
                    FriendState.Success(message)
                onEvent(
                    FriendEvent.LoadFriends(user1)
                )

            } catch (e: Exception) {

                _state.value =
                    FriendState.Error(
                        e.message ?: "Unknown error"
                    )
            }
        }
    }
    fun clearStatusMessage() {

        uiState = uiState.copy(
            statusMessage = null
        )
    }
}