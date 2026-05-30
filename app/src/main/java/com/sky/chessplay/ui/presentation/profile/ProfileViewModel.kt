package com.sky.chessplay.ui.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sky.chessplay.domain.model.auth.User
import com.sky.chessplay.domain.model.profile.GameHistoryItem
import com.sky.chessplay.domain.model.profile.UserStats
import com.sky.chessplay.domain.repository.AuthRepository
import com.sky.chessplay.domain.repository.FriendRepository
import com.sky.chessplay.domain.repository.GameRepository
import com.sky.chessplay.data.remote.api.ProfileApi
import com.sky.chessplay.data.mapper.ProfileMapper.toDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileState(
    val isLoading: Boolean = false,
    val error: String? = null,

    val user: User? = null,
    val friendsCount: Int = 0,

    val stats: UserStats? = null,
    val history: List<GameHistoryItem> = emptyList(),
    val filteredHistory: List<GameHistoryItem> = emptyList(),

    val filterOpponent: String = "",
    val filterResult: String = "ALL"
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileApi: ProfileApi,
    private val gameRepository: GameRepository,
    private val friendRepository: FriendRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state

    fun loadProfile() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            try {
                val authResult = authRepository.getMe()
                if (authResult.isFailure) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = authResult.exceptionOrNull()?.message ?: "Failed to load user"
                    )
                    return@launch
                }

                val user = authResult.getOrNull() ?: throw Exception("Empty user")
                val statsResponse = profileApi.getProfile()
                val stats = statsResponse.toDomain()
                val historyResult = gameRepository.getHistory(user.id.toString())
                val history = historyResult.getOrNull().orEmpty()

                val filtered = applyFilters(history, _state.value.filterOpponent, _state.value.filterResult)
                val friends = friendRepository.getFriends(user.id)

                _state.value = _state.value.copy(
                    user = user,
                    friendsCount = friends.size,
                    stats = stats,
                    history = history,
                    filteredHistory = filtered,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun onFilterOpponentChanged(value: String) {
        _state.value = _state.value.copy(
            filterOpponent = value,
            filteredHistory = applyFilters(_state.value.history, value, _state.value.filterResult)
        )
    }

    fun onFilterResultChanged(value: String) {
        _state.value = _state.value.copy(
            filterResult = value,
            filteredHistory = applyFilters(_state.value.history, _state.value.filterOpponent, value)
        )
    }

    fun resetFilters() {
        _state.value = _state.value.copy(
            filterOpponent = "",
            filterResult = "ALL",
            filteredHistory = _state.value.history
        )
    }

    private fun applyFilters(
        history: List<GameHistoryItem>,
        opponent: String,
        result: String
    ): List<GameHistoryItem> {
        return history.filter { game ->
            val opponentMatch = opponent.isBlank() || game.opponentName?.contains(opponent, ignoreCase = true) == true
            val resultMatch = when (result.uppercase()) {
                "ALL" -> true
                "WIN", "THẮNG" -> game.result.contains("WIN", ignoreCase = true)
                "LOSS", "THUA" -> game.result.contains("LOSS", ignoreCase = true)
                "DRAW", "HÒA" -> game.result.contains("DRAW", ignoreCase = true)
                else -> true
            }
            opponentMatch && resultMatch
        }
    }
}
