package com.sky.chessplay.ui.presentation.profile

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sky.chessplay.data.mapper.ProfileMapper.toDomain
import com.sky.chessplay.data.remote.api.ProfileApi
import com.sky.chessplay.domain.model.auth.User
import com.sky.chessplay.domain.model.profile.GameHistoryItem
import com.sky.chessplay.domain.model.profile.UserStats
import com.sky.chessplay.domain.repository.AuthRepository
import com.sky.chessplay.domain.repository.FriendRepository
import com.sky.chessplay.domain.repository.GameRepository
import com.sky.chessplay.domain.repository.LocalMatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class HistoryType {
    ONLINE, LOCAL, TOURNAMENT
}

data class ProfileState(
    val isLoading: Boolean = false,
    val error: String? = null,

    val user: User? = null,
    val friendsCount: Int = 0,

    val stats: UserStats? = null,
    val historyType: HistoryType = HistoryType.ONLINE,
    val onlineHistory: List<GameHistoryItem> = emptyList(),
    val localHistory: List<GameHistoryItem> = emptyList(),
    val filteredHistory: List<GameHistoryItem> = emptyList(),

    val filterOpponent: String = "",
    val filterResult: String = "ALL"
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileApi: ProfileApi,
    private val gameRepository: GameRepository,
    private val friendRepository: FriendRepository,
    private val localMatchRepository: LocalMatchRepository
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

                // Load online history
                val historyResult = gameRepository.getHistory(user.id.toString())
                val onlineHistory = historyResult.getOrNull().orEmpty()

                // Load local history from Room
                val localMatches = localMatchRepository.getLocalMatches()
                val localHistory = localMatches.map { entity ->
                    val dateStr = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        try {
                            val instant = java.time.Instant.ofEpochMilli(entity.playedAt)
                            val zonedDateTime = java.time.ZonedDateTime.ofInstant(instant, java.time.ZoneId.systemDefault())
                            zonedDateTime.toString()
                        } catch (e: Exception) {
                            entity.playedAt.toString()
                        }
                    } else {
                        entity.playedAt.toString()
                    }

                    GameHistoryItem(
                        gameId = "local_${entity.id}",
                        opponentName = "Player 2",
                        myColor = "WHITE",
                        result = entity.result,
                        pgn = entity.moveHistory,
                        playedAt = dateStr
                    )
                }

                val activeHistory = when (_state.value.historyType) {
                    HistoryType.ONLINE -> onlineHistory
                    HistoryType.LOCAL -> localHistory
                    HistoryType.TOURNAMENT -> emptyList()
                }
                val filtered = applyFilters(activeHistory, _state.value.filterOpponent, _state.value.filterResult)
                val friends = friendRepository.getFriends(user.id)

                _state.value = _state.value.copy(
                    user = user,
                    friendsCount = friends.size,
                    stats = stats,
                    onlineHistory = onlineHistory,
                    localHistory = localHistory,
                    filteredHistory = filtered,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun setHistoryType(type: HistoryType) {
        val activeHistory = when (type) {
            HistoryType.ONLINE -> _state.value.onlineHistory
            HistoryType.LOCAL -> _state.value.localHistory
            HistoryType.TOURNAMENT -> emptyList()
        }
        _state.value = _state.value.copy(
            historyType = type,
            filteredHistory = applyFilters(activeHistory, _state.value.filterOpponent, _state.value.filterResult)
        )
    }

    fun onFilterOpponentChanged(value: String) {
        val activeHistory = when (_state.value.historyType) {
            HistoryType.ONLINE -> _state.value.onlineHistory
            HistoryType.LOCAL -> _state.value.localHistory
            HistoryType.TOURNAMENT -> emptyList()
        }
        _state.value = _state.value.copy(
            filterOpponent = value,
            filteredHistory = applyFilters(activeHistory, value, _state.value.filterResult)
        )
    }

    fun onFilterResultChanged(value: String) {
        val activeHistory = when (_state.value.historyType) {
            HistoryType.ONLINE -> _state.value.onlineHistory
            HistoryType.LOCAL -> _state.value.localHistory
            HistoryType.TOURNAMENT -> emptyList()
        }
        _state.value = _state.value.copy(
            filterResult = value,
            filteredHistory = applyFilters(activeHistory, _state.value.filterOpponent, value)
        )
    }

    fun resetFilters() {
        val activeHistory = when (_state.value.historyType) {
            HistoryType.ONLINE -> _state.value.onlineHistory
            HistoryType.LOCAL -> _state.value.localHistory
            HistoryType.TOURNAMENT -> emptyList()
        }
        _state.value = _state.value.copy(
            filterOpponent = "",
            filterResult = "ALL",
            filteredHistory = activeHistory
        )
    }

    private fun applyFilters(
        history: List<GameHistoryItem>,
        opponent: String,
        result: String
    ): List<GameHistoryItem> {

        return history.filter { game ->

            val opponentMatch =
                opponent.isBlank() ||
                        game.opponentName?.contains(opponent, true) == true

            val isWin =
                (game.result == "1-0" && game.myColor == "WHITE") ||
                        (game.result == "0-1" && game.myColor == "BLACK")

            val isLoss =
                (game.result == "1-0" && game.myColor == "BLACK") ||
                        (game.result == "0-1" && game.myColor == "WHITE")

            val isDraw =
                game.result == "1/2-1/2" ||
                        game.result == "0.5-0.5"

            val resultMatch = when (result.uppercase()) {
                "", "ALL" -> true
                "WIN", "THẮNG" -> isWin
                "LOSS", "THUA" -> isLoss
                "DRAW", "HÒA" -> isDraw
                else -> true
            }
            opponentMatch && resultMatch
        }
    }
}
