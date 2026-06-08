package com.sky.chessplay.data.repository

import com.sky.chessplay.data.mapper.toDomain
import com.sky.chessplay.data.remote.api.TournamentApi
import com.sky.chessplay.domain.model.tournament.Standing
import com.sky.chessplay.domain.model.tournament.Tournament
import com.sky.chessplay.domain.repository.TournamentRepository
import javax.inject.Inject

class TournamentRepositoryImpl @Inject constructor(
    private val api: TournamentApi
) : TournamentRepository {

    override suspend fun getTournaments(): Result<List<Tournament>> {
        return try {
            val response = api.getTournaments()
            if (response.isSuccessful) {

                val tournaments = response.body()
                    ?.map { it.toDomain() }
                    ?: emptyList()

                Result.success(tournaments)

            } else {
                Result.failure(
                    Exception("Failed to load tournaments")
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun joinTournament(
        tournamentId: Long
    ): Result<Unit> {
        return try {
            val response = api.joinTournament(tournamentId)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(
                    Exception("Join tournament failed")
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun leaveTournament(
        tournamentId: Long
    ): Result<Unit> {
        return try {
            val response = api.leaveTournament(tournamentId)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(
                    Exception("Leave tournament failed")
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getStandings(
        tournamentId: Long
    ): Result<List<Standing>> {
        return try {
            val response = api.getStandings(tournamentId)

            if (response.isSuccessful) {

                val standings = response.body()
                    ?.map { it.toDomain() }
                    ?: emptyList()

                Result.success(standings)

            } else {
                Result.failure(
                    Exception("Failed to load standings")
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}