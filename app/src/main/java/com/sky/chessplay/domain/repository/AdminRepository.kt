package com.sky.chessplay.domain.repository

import com.sky.chessplay.data.remote.dto.response.AdminDashboardResponse
import com.sky.chessplay.data.remote.dto.response.UserAdminResponse
import com.sky.chessplay.data.remote.dto.response.TournamentResponse as Tournament
import TournamentRequest

interface AdminRepository {

    suspend fun getDashboardStats(): Result<AdminDashboardResponse>

    suspend fun getUsers(query: String? = null): Result<List<UserAdminResponse>>

    suspend fun getUserProfile(userId: Long): Result<UserAdminResponse>

    suspend fun banUser(userId: Long): Result<Unit>

    suspend fun unbanUser(userId: Long): Result<Unit>

    suspend fun getTournaments(): Result<List<Tournament>>

    suspend fun createTournament(tournament: TournamentRequest): Result<Tournament>

    suspend fun updateTournament(tournamentId: Long, tournament: TournamentRequest): Result<Tournament>

    suspend fun startTournament(tournamentId: Long): Result<Unit>

    suspend fun finishTournament(tournamentId: Long): Result<Unit>

    suspend fun cancelTournament(tournamentId: Long): Result<Unit>

    suspend fun submitPairingResult(pairingId: Long, result: Map<String, String>): Result<Unit>
}
