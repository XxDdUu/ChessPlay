package com.sky.chessplay.ui.presentation.admin

import TournamentRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sky.chessplay.domain.state.AdminTab
import com.sky.chessplay.domain.state.AdminUiState
import com.sky.chessplay.domain.usecases.BanUserUseCase
import com.sky.chessplay.domain.usecases.CancelTournamentUseCase
import com.sky.chessplay.domain.usecases.CreateTournamentsUseCase
import com.sky.chessplay.domain.usecases.FinishTournamentUseCase
import com.sky.chessplay.domain.usecases.GetAdminDashboardUseCase
import com.sky.chessplay.domain.usecases.GetAdminUsersUseCase
import com.sky.chessplay.domain.usecases.GetTournamentsUseCase
import com.sky.chessplay.domain.usecases.StartTournamentUseCase
import com.sky.chessplay.domain.usecases.UnbanUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val getDashboardUseCase: GetAdminDashboardUseCase,
    private val getUsersUseCase: GetAdminUsersUseCase,
    private val getTournamentsUseCase: GetTournamentsUseCase,
    private val createTournamentsUseCase: CreateTournamentsUseCase,
    private val banUserUseCase: BanUserUseCase,
    private val unbanUserUseCase: UnbanUserUseCase,
    private val finishTournamentUseCase: FinishTournamentUseCase,
    private val startTournamentUseCase: StartTournamentUseCase,
    private val cancelTournamentUseCase: CancelTournamentUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun selectTab(tab: AdminTab) {
        _uiState.update { it.copy(selectedTab = tab) }
        when (tab) {
            AdminTab.Dashboard -> loadDashboard()
            AdminTab.Users -> loadUsers()
            AdminTab.Tournaments -> loadTournaments()
        }
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getDashboardUseCase()
                .onSuccess { stats ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            dashboardStats = stats
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message
                        )
                    }
                }
        }
    }

    fun loadUsers(query: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getUsersUseCase(query)
                .onSuccess { users ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            users = users
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message
                        )
                    }
                }
        }
    }
    fun createTournament(
        request: TournamentRequest
    ) {
        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }

            createTournamentsUseCase(request)
                .onSuccess {

                    _uiState.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    loadTournaments()
                }
                .onFailure { error ->

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message
                        )
                    }
                }
        }
    }

    fun loadTournaments() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getTournamentsUseCase()
                .onSuccess { tournaments ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            tournaments = tournaments
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message
                        )
                    }
                }
        }
    }

    fun banUser(userId: Long) {
        viewModelScope.launch {
            banUserUseCase(userId)
                .onSuccess {
                    loadUsers()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(error = error.message)
                    }
                }
        }
    }

    fun unbanUser(userId: Long) {
        viewModelScope.launch {
            unbanUserUseCase(userId)
                .onSuccess {
                    loadUsers()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(error = error.message)
                    }
                }
        }
    }

    fun finishTournament(tournamentId: Long) {
        viewModelScope.launch {
            finishTournamentUseCase(tournamentId)
                .onSuccess {
                    loadTournaments()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(error = error.message)
                    }
                }
        }
    }

    fun cancelTournament(tournamentId: Long) {
        viewModelScope.launch {
            cancelTournamentUseCase(tournamentId)
                .onSuccess {
                    loadTournaments()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(error = error.message)
                    }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
