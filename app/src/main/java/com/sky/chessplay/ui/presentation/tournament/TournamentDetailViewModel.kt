package com.sky.chessplay.ui.presentation.tournament

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sky.chessplay.domain.state.TournamentDetailUiState
import com.sky.chessplay.domain.usecases.GetStandingsUseCase
import com.sky.chessplay.domain.usecases.GetTournamentByIdUseCase
import com.sky.chessplay.domain.usecases.GetTournamentPairingsUseCase
import com.sky.chessplay.domain.usecases.GetTournamentRoundsUseCase
import com.sky.chessplay.domain.usecases.JoinTournamentUseCase
import com.sky.chessplay.domain.usecases.LeaveTournamentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TournamentDetailViewModel @Inject constructor(
    private val getTournamentByIdUseCase: GetTournamentByIdUseCase,
    private val getStandingsUseCase: GetStandingsUseCase,
    private val getTournamentRoundsUseCase: GetTournamentRoundsUseCase,
    private val getTournamentPairingsUseCase: GetTournamentPairingsUseCase,
    private val joinTournamentUseCase: JoinTournamentUseCase,
    private val leaveTournamentUseCase: LeaveTournamentUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TournamentDetailUiState())
    val uiState = _uiState.asStateFlow()

    fun loadTournament(tournamentId: Long) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    roundsError = null
                )
            }

            val detailDeferred = async { getTournamentByIdUseCase(tournamentId) }
            val standingsDeferred = async { getStandingsUseCase(tournamentId) }
            val roundsDeferred = async { getTournamentRoundsUseCase(tournamentId) }

            val detailResult = detailDeferred.await()
            val standingsResult = standingsDeferred.await()
            val roundsResult = roundsDeferred.await()

            val tournament = detailResult.getOrNull()
            val standings = standingsResult.getOrNull().orEmpty()
            val rounds = roundsResult.getOrNull().orEmpty()
            val selectedRound = rounds.lastOrNull()

            _uiState.update {
                it.copy(
                    isLoading = false,
                    tournament = tournament,
                    standings = standings,
                    rounds = rounds,
                    selectedRoundId = selectedRound?.id,
                    pairings = emptyList(),
                    roundsError = roundsResult.exceptionOrNull()?.message,
                    error = detailResult.exceptionOrNull()?.message
                        ?: standingsResult.exceptionOrNull()?.message
                )
            }

            if (selectedRound != null) {
                loadPairings(tournamentId, selectedRound.id)
            }
        }
    }

    fun loadPairings(tournamentId: Long, roundId: Long) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isPairingsLoading = true,
                    selectedRoundId = roundId,
                    pairings = emptyList()
                )
            }

            getTournamentPairingsUseCase(tournamentId, roundId)
                .onSuccess { pairings ->
                    _uiState.update {
                        it.copy(
                            isPairingsLoading = false,
                            pairings = pairings,
                            roundsError = null
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isPairingsLoading = false,
                            roundsError = error.message
                        )
                    }
                }
        }
    }

    fun joinTournament(tournamentId: Long) {
        viewModelScope.launch {
            joinTournamentUseCase(tournamentId)
                .onSuccess { loadTournament(tournamentId) }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message) }
                }
        }
    }

    fun leaveTournament(tournamentId: Long) {
        viewModelScope.launch {
            leaveTournamentUseCase(tournamentId)
                .onSuccess { loadTournament(tournamentId) }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message) }
                }
        }
    }
}
