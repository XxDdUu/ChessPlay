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
import com.sky.chessplay.domain.state.FriendshipStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendViewModel @Inject constructor(
    private val repository: FriendRepository,
    private val chessSocket: ChessSocket,
    private val profileApi: com.sky.chessplay.data.remote.api.ProfileApi
) : ViewModel() {

    private val _state = MutableStateFlow<FriendState>(FriendState.Idle)
    val state: StateFlow<FriendState> = _state

    var uiState by mutableStateOf(FriendUiState())
        private set

    var friendsList by mutableStateOf<List<com.sky.chessplay.data.remote.dto.response.FriendResponse>>(emptyList())
        private set

    var pendingRequestsList by mutableStateOf<List<com.sky.chessplay.data.remote.dto.response.FriendResponse>>(emptyList())
        private set

    var searchResultList by mutableStateOf<List<com.sky.chessplay.data.remote.dto.response.UserSearchResponse>>(emptyList())
        private set

    var isSearching by mutableStateOf(false)
        private set

    var leaderboardList by mutableStateOf<List<com.sky.chessplay.data.remote.dto.response.LeaderboardResponse>>(emptyList())
        private set

    var isLeaderboardLoading by mutableStateOf(false)
        private set

    var selectedPlayerStats by mutableStateOf<com.sky.chessplay.data.remote.dto.response.UserProfileResponse?>(null)
        private set

    var isPlayerStatsLoading by mutableStateOf(false)
        private set

    private var currentUserId: Long? = null

    var isRefreshing by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
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
                errorMessage = null
            }
            is FriendEvent.SearchFriend -> {
                currentUserId?.let { userId ->
                    searchNewFriends(userId, event.query)
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
            is FriendEvent.RemoveFriend -> {
                removeFriend(
                    event.user1,
                    event.user2
                )
            }

            is FriendEvent.ClearSearchResults -> {
                searchResultList = emptyList()

                uiState = uiState.copy(
                    searchQuery = "",
                    searchResults = emptyList()
                )
            }
        }
    }

    fun loadLeaderboard() {
        viewModelScope.launch {
            isLeaderboardLoading = true
            try {
                leaderboardList = profileApi.getLeaderboard()
            } catch (e: Exception) {
                Log.e("FriendViewModel", "Failed to load leaderboard", e)
            } finally {
                isLeaderboardLoading = false
            }
        }
    }

    fun loadPlayerStats(playerId: Long) {
        viewModelScope.launch {
            isPlayerStatsLoading = true
            try {
                selectedPlayerStats = profileApi.getUserStats(playerId)
            } catch (e: Exception) {
                Log.e("FriendViewModel", "Failed to load player stats", e)
            } finally {
                isPlayerStatsLoading = false
            }
        }
    }

    fun clearPlayerStats() {
        selectedPlayerStats = null
    }

    private fun loadFriends(userId: Long) {
        currentUserId = userId
        loadLeaderboard()
        viewModelScope.launch {
            _state.value = FriendState.Loading
            isRefreshing = true
            errorMessage = null
            try {
                val friends = repository.getFriends(userId)
                Log.d("LOAD FRIEND DEBUG", friends.toString())
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
                
                searchResultList = searchResultList.map { u ->
                    if (u.userId == receiverId) {
                        u.copy(friendshipStatus = FriendshipStatus.PENDING_SENT)
                    } else {
                        u
                    }
                }
                selectedPlayerStats?.let { stats ->
                    if (stats.userId.toLong() == receiverId) {
                        selectedPlayerStats = stats.copy(friendshipStatus = "PENDING_SENT")
                    }
                }
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isSendingRequest = false,
                    statusMessage = e.message ?: "Failed to send friend request."
                )
            }
        }
    }

    private fun searchNewFriends(userId: Long, query: String) {
        if (query.isBlank()) {
            searchResultList = emptyList()
            return
        }
        viewModelScope.launch {
            isSearching = true
            try {
                searchResultList = repository.searchNewFriends(userId, query)
            } catch (e: Exception) {
                Log.e("FriendViewModel", "Search failed", e)
            } finally {
                isSearching = false
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
                
                searchResultList = searchResultList.map { u ->
                    if (u.userId == user2) {
                        u.copy(friendshipStatus = FriendshipStatus.ACCEPTED)
                    } else {
                        u
                    }
                }
                selectedPlayerStats?.let { stats ->
                    if (stats.userId.toLong() == user2) {
                        selectedPlayerStats = stats.copy(friendshipStatus = "ACCEPTED")
                    }
                }

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

    fun clearStatusMessage() {
        uiState = uiState.copy(statusMessage = null)
    }
}