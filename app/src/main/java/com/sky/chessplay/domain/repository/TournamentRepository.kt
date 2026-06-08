package com.sky.chessplay.domain.repository

import com.sky.chessplay.domain.model.tournament.Standing
import com.sky.chessplay.domain.model.tournament.Tournament


interface TournamentRepository {

    suspend fun getTournaments(): Result<List<Tournament>>

    suspend fun joinTournament(
        tournamentId: Long
    ): Result<Unit>

    suspend fun leaveTournament(
        tournamentId: Long
    ): Result<Unit>

    suspend fun getStandings(
        tournamentId: Long
    ): Result<List<Standing>>
}