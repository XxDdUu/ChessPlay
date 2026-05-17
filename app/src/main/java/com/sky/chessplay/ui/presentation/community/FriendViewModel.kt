package com.sky.chessplay.ui.presentation.community

import FriendEvent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sky.chessplay.domain.repository.FriendRepository
import com.sky.chessplay.domain.state.FriendState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendViewModel @Inject constructor(
    private val repository: FriendRepository
) : ViewModel() {

    private val _state =
        MutableStateFlow<FriendState>(FriendState.Idle)

    val state: StateFlow<FriendState> = _state

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

            }

            is FriendEvent.ConnectFriend -> {

            }
        }
    }

    private fun loadFriends(userId: Long) {

        viewModelScope.launch {

            _state.value = FriendState.Loading

            try {

                val friends =
                    repository.getFriends(userId)

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

    private fun sendFriendRequest(
        senderId: Long,
        receiverId: Long
    ) {

        viewModelScope.launch {

            _state.value = FriendState.Loading

            try {

                val message =
                    repository.sendFriendRequest(
                        senderId,
                        receiverId
                    )

                _state.value =
                    FriendState.Success(message)

            } catch (e: Exception) {

                _state.value =
                    FriendState.Error(
                        e.message ?: "Unknown error"
                    )
            }
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

            } catch (e: Exception) {

                _state.value =
                    FriendState.Error(
                        e.message ?: "Unknown error"
                    )
            }
        }
    }
}