package com.sky.chessplay.data.remote.api

import com.sky.chessplay.data.remote.dto.response.StandingResponse
import com.sky.chessplay.data.remote.dto.response.TournamentPairingResponse
import com.sky.chessplay.data.remote.dto.response.TournamentResponse as Tournament
import com.sky.chessplay.data.remote.dto.response.TournamentRoundResponse
import com.sky.chessplay.data.remote.dto.response.MyPairingResponse
import okhttp3.ResponseBody
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

    @GET("api/tournaments/{id}/rounds")
    suspend fun getRounds(
        @Path("id") tournamentId: Long
    ): Response<List<TournamentRoundResponse>>

    @GET("api/tournaments/{id}/pairings/{roundId}")
    suspend fun getPairings(
        @Path("id") tournamentId: Long,
        @Path("roundId") roundId: Long
    ): Response<List<TournamentPairingResponse>>

    @POST("api/tournaments/{id}/join")
    suspend fun joinTournament(
        @Path("id") tournamentId: Long
    ): Response<ResponseBody>

    @POST("api/tournaments/{id}/leave")
    suspend fun leaveTournament(
        @Path("id") tournamentId: Long
    ): Response<ResponseBody>

    @GET("api/tournaments/{id}/standings")
    suspend fun getStandings(
        @Path("id") tournamentId: Long
    ): Response<List<StandingResponse>>

    @GET("api/tournaments/{id}/my-pairing")
    suspend fun getMyPairing(
        @Path("id") tournamentId: Long
    ): Response<MyPairingResponse>
}
