package com.sky.chessplay.data.remote.api

import com.sky.chessplay.data.remote.dto.response.StandingResponse
import com.sky.chessplay.data.remote.dto.response.TournamentResponse as Tournament
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TournamentApi {

    @GET("api/tournaments")
    suspend fun getTournaments(): Response<List<Tournament>>

    @GET("api/tournaments/{id}")
    suspend fun getTournamentById(
        @Path("id") tournamentId: Long
    ): Response<Tournament>

    @POST("api/tournaments/{id}/join")
    suspend fun joinTournament(
        @Path("id") tournamentId: Long
    ): Response<Unit>

    @POST("api/tournaments/{id}/leave")
    suspend fun leaveTournament(
        @Path("id") tournamentId: Long
    ): Response<Unit>

    @GET("api/tournaments/{id}/standings")
    suspend fun getStandings(
        @Path("id") tournamentId: Long
    ): Response<List<StandingResponse>>
}