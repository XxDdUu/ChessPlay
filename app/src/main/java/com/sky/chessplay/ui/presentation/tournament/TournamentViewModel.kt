package com.sky.chessplay.ui.presentation.tournament

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sky.chessplay.domain.state.TournamentUiState
import com.sky.chessplay.domain.usecases.GetStandingsUseCase
import com.sky.chessplay.domain.usecases.GetTournamentsUseCase
import com.sky.chessplay.domain.usecases.JoinTournamentUseCase
import com.sky.chessplay.domain.usecases.LeaveTournamentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TournamentViewModel @Inject constructor(
    private val getTournamentsUseCase: GetTournamentsUseCase,
    private val joinTournamentUseCase: JoinTournamentUseCase,
    private val leaveTournamentUseCase: LeaveTournamentUseCase,
    private val getStandingsUseCase: GetStandingsUseCase
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(TournamentUiState())

    val uiState = _uiState.asStateFlow()

    init {
        loadTournaments()
    }

    fun loadTournaments() {
        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }

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

    fun joinTournament(
        tournamentId: Long
    ) {
        viewModelScope.launch {

            joinTournamentUseCase(tournamentId)
                .onSuccess {
                    loadTournaments()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            error = error.message
                        )
                    }
                }
        }
    }

    fun leaveTournament(
        tournamentId: Long
    ) {
        viewModelScope.launch {

            leaveTournamentUseCase(tournamentId)
                .onSuccess {

                    loadTournaments()
                }
                .onFailure { error ->

                    _uiState.update {
                        it.copy(
                            error = error.message
                        )
                    }
                }
        }
    }

    fun loadStandings(
        tournamentId: Long
    ) {
        viewModelScope.launch {

            getStandingsUseCase(tournamentId)
                .onSuccess { standings ->

                    _uiState.update {
                        it.copy(
                            standings = standings
                        )
                    }
                }
                .onFailure { error ->

                    _uiState.update {
                        it.copy(
                            error = error.message
                        )
                    }
                }
        }
    }
}