package com.sky.chessplay.data.repository

import TournamentRequest
import com.sky.chessplay.data.remote.api.AdminApi
import com.sky.chessplay.data.remote.dto.response.AdminDashboardResponse
import com.sky.chessplay.data.remote.dto.response.TournamentResponse as Tournament
import com.sky.chessplay.data.remote.dto.response.UserAdminResponse
import com.sky.chessplay.domain.repository.AdminRepository
import javax.inject.Inject

class AdminRepositoryImpl @Inject constructor(
    private val api: AdminApi
) : AdminRepository {

    override suspend fun getDashboardStats(): Result<AdminDashboardResponse> {
        return try {
            val response = api.getDashboardStats()
            if (response.isSuccessful) {
                Result.success(response.body() ?: error("Empty response"))
            } else {
                Result.failure(Exception("Failed to load dashboard stats"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUsers(query: String?): Result<List<UserAdminResponse>> {
        return try {
            val response = api.getUsers(query)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to load users"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserProfile(userId: Long): Result<UserAdminResponse> {
        return try {
            val response = api.getUserProfile(userId)
            if (response.isSuccessful) {
                Result.success(response.body() ?: error("Empty response"))
            } else {
                Result.failure(Exception("Failed to load user profile"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun banUser(userId: Long): Result<Unit> {
        return try {
            val response = api.banUser(userId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to ban user"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun unbanUser(userId: Long): Result<Unit> {
        return try {
            val response = api.unbanUser(userId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to unban user"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTournaments(): Result<List<Tournament>> {
        return try {
            val response = api.getTournaments()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to load tournaments"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createTournament(tournament: TournamentRequest): Result<Tournament> {
        return try {
            val response = api.createTournament(tournament)
            if (response.isSuccessful) {
                Result.success(response.body() ?: error("Empty response"))
            } else {
                Result.failure(Exception("Failed to create tournament"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTournament(tournamentId: Long, tournament: TournamentRequest): Result<Tournament> {
        return try {
            val response = api.updateTournament(tournamentId, tournament)
            if (response.isSuccessful) {
                Result.success(response.body() ?: error("Empty response"))
            } else {
                Result.failure(Exception("Failed to update tournament"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun startTournament(tournamentId: Long): Result<Unit> {
        return try {
            val response = api.startTournament(tournamentId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to start tournament"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun finishTournament(tournamentId: Long): Result<Unit> {
        return try {
            val response = api.finishTournament(tournamentId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to finish tournament"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelTournament(tournamentId: Long): Result<Unit> {
        return try {
            val response = api.cancelTournament(tournamentId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to cancel tournament"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun submitPairingResult(pairingId: Long, result: Map<String, String>): Result<Unit> {
        return try {
            val response = api.submitPairingResult(pairingId, result)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to submit pairing result"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
