package com.sky.chessplay.ui.presentation.community

import FriendEvent
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sky.chessplay.data.remote.dto.response.UserSearchResponse
import com.sky.chessplay.domain.repository.FriendRepository
import com.sky.chessplay.domain.socket.ChessSocket
import com.sky.chessplay.domain.socket.SocketEvent
import com.sky.chessplay.domain.state.FriendState
import com.sky.chessplay.domain.state.FriendUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class FriendViewModel @Inject constructor(
    private val repository: FriendRepository,
    private val chessSocket: ChessSocket
) : ViewModel() {

    private val _state = MutableStateFlow<FriendState>(FriendState.Idle)
    val state: StateFlow<FriendState> = _state

    var uiState by mutableStateOf(FriendUiState())
        private set

    var friendsList by mutableStateOf<List<com.sky.chessplay.data.remote.dto.response.FriendResponse>>(emptyList())
        private set

    var pendingRequestsList by mutableStateOf<List<com.sky.chessplay.data.remote.dto.response.FriendResponse>>(emptyList())
        private set

    var isRefreshing by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set
    var searchResults: List<UserSearchResponse> by mutableStateOf(emptyList())
        private set
    private val searchFlow = MutableSharedFlow<FriendEvent.SearchFriend>()
    var isSearching by mutableStateOf(false)
        private set
    init {

        viewModelScope.launch {
            chessSocket.socketEvents.collect { event ->
                if (event is SocketEvent.FriendPresence) {
                    updateFriendPresence(event)
                }
            }
        }

        viewModelScope.launch {
            searchFlow
                .debounce(500)
                .distinctUntilChanged()
                .collectLatest { event ->
                    searchFriends(
                        event.userId,
                        event.query
                    )
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
                errorMessage = null
            }
            is FriendEvent.SearchFriend -> {
                isSearching = true
                viewModelScope.launch {
                    searchFlow.emit(event)
                }
            }
            is FriendEvent.SendChallenge -> {
                Log.d(
                    "INVITE FRIEND DEBUG",
                    "${event.friendId} ${event.matchType}"
                )
                chessSocket.inviteFriend(
                    friendId = event.friendId,
                    matchType = event.matchType
                )
            }
            is FriendEvent.ConnectFriend -> {
                // Not implemented or stubbed
            }
            is FriendEvent.ClearSearchResults -> {
                searchResults = emptyList()

                uiState = uiState.copy(
                    searchQuery = "",
                    searchResults = emptyList()
                )
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
            isRefreshing = true
            errorMessage = null
            try {
                val friends = repository.getFriends(userId)
                friendsList = friends
                _state.value = FriendState.FriendsLoaded(friends)
            } catch (e: Exception) {
                errorMessage = e.message ?: "Unknown error"
                _state.value = FriendState.Error(e.message ?: "Unknown error")
            } finally {
                isRefreshing = false
            }
        }
    }

    private fun loadPendingRequests(userId: Long) {
        viewModelScope.launch {
            try {
                val pending = repository.getPendingRequests(userId)
                pendingRequestsList = pending
                _state.value = FriendState.PendingLoaded(pending)
            } catch (e: Exception) {
                // Ignore or handle
            }
        }
    }

    fun sendFriendRequest(
        senderId: Long,
        receiverId: Long
    ) {
        if (uiState.isSendingRequest) return
        viewModelScope.launch {
            uiState = uiState.copy(isSendingRequest = true)
            try {
                val message = repository.sendFriendRequest(senderId, receiverId)
                uiState = uiState.copy(
                    isSendingRequest = false,
                    friendRequestSent = true,
                    statusMessage = message
                )
                _state.value = FriendState.FriendRequestSent(message)
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isSendingRequest = false,
                    statusMessage = e.message ?: "Failed to send friend request."
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
                repository.removeFriend(user1, user2)
                loadFriends(user1)
            } catch (e: Exception) {
                // Ignore
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
        friendsList = friendsList.map { friend ->
            if (friend.userId == presence.userId) {
                friend.copy(status = if (presence.online) "ONLINE" else "OFFLINE")
            } else {
                friend
            }
        }
    }

    private fun acceptFriendRequest(
        user1: Long,
        user2: Long
    ) {
        viewModelScope.launch {
            _state.value = FriendState.Loading
            isRefreshing = true
            try {
                val message = repository.acceptFriendRequest(user1, user2)
                _state.value = FriendState.Success(message)
                loadFriends(user1)
                loadPendingRequests(user1)
            } catch (e: Exception) {
                errorMessage = e.message ?: "Unknown error"
                _state.value = FriendState.Error(e.message ?: "Unknown error")
            } finally {
                isRefreshing = false
            }
        }
    }
    private fun searchFriends(
        userId: Long,
        query: String
    ) {
        if (query.isBlank()) {
            searchResults = emptyList()
            isSearching = false
            return
        }

        viewModelScope.launch {
            _state.value = FriendState.Loading
            isSearching = true
            repository.searchFriends(userId, query)
                .onSuccess { result ->
                    searchResults = result

                    uiState = uiState.copy(
                        searchQuery = query,
                        searchResults = result
                    )

                    _state.value = FriendState.Success("Searched for $query")
                }
                .onFailure { error ->
                    errorMessage = error.message
                    _state.value = FriendState.Error(
                        error.message ?: "Search failed"
                    )
                }
                .also {
                    isSearching = false
                }
        }
    }
    fun clearStatusMessage() {
        uiState = uiState.copy(statusMessage = null)
    }
}