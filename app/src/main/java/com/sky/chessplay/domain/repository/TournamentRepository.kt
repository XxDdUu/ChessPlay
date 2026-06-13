package com.sky.chessplay.domain.repository

import com.sky.chessplay.domain.model.tournament.Standing
import com.sky.chessplay.domain.model.tournament.Tournament
import com.sky.chessplay.domain.model.tournament.TournamentPairing
import com.sky.chessplay.domain.model.tournament.TournamentRound


interface TournamentRepository {

    suspend fun getTournaments(): Result<List<Tournament>>

    suspend fun getTournamentById(
        tournamentId: Long
    ): Result<Tournament>

    suspend fun joinTournament(
        tournamentId: Long
    ): Result<Unit>

    suspend fun leaveTournament(
        tournamentId: Long
    ): Result<Unit>

    suspend fun getStandings(
        tournamentId: Long
    ): Result<List<Standing>>

    suspend fun getRounds(
        tournamentId: Long
    ): Result<List<TournamentRound>>

    suspend fun getPairings(
        tournamentId: Long,
        roundId: Long
    ): Result<List<TournamentPairing>>

    suspend fun getMyPairing(
        tournamentId: Long
    ): Result<com.sky.chessplay.domain.model.tournament.MyPairing?>
}
