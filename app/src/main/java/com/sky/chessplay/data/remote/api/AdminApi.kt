package com.sky.chessplay.data.remote.api

import TournamentRequest
import com.sky.chessplay.data.remote.dto.response.AdminDashboardResponse
import com.sky.chessplay.data.remote.dto.response.TournamentResponse as Tournament
import com.sky.chessplay.data.remote.dto.response.UserAdminResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface AdminApi {

    @GET("api/admin/stats")
    suspend fun getDashboardStats(): Response<AdminDashboardResponse>

    @GET("api/admin/users")
    suspend fun getUsers(
        @Query("query") query: String? = null
    ): Response<List<UserAdminResponse>>

    @GET("api/admin/users/{userId}")
    suspend fun getUserProfile(
        @Path("userId") userId: Long
    ): Response<UserAdminResponse>

    @POST("api/admin/users/{userId}/ban")
    suspend fun banUser(
        @Path("userId") userId: Long
    ): Response<ResponseBody>

    @POST("api/admin/users/{userId}/unban")
    suspend fun unbanUser(
        @Path("userId") userId: Long
    ): Response<ResponseBody>

    @GET("api/admin/tournaments")
    suspend fun getTournaments(): Response<List<Tournament>>

    @POST("api/admin/tournaments")
    suspend fun createTournament(
        @Body tournament: TournamentRequest
    ): Response<Tournament>

    @PUT("api/admin/tournaments/{tournamentId}")
    suspend fun updateTournament(
        @Path("tournamentId") tournamentId: Long,
        @Body tournament: TournamentRequest
    ): Response<Tournament>

    @POST("api/admin/tournaments/{tournamentId}/start")
    suspend fun startTournament(
        @Path("tournamentId") tournamentId: Long
    ): Response<ResponseBody>

    @POST("api/admin/tournaments/{tournamentId}/finish")
    suspend fun finishTournament(
        @Path("tournamentId") tournamentId: Long
    ): Response<ResponseBody>

    @DELETE("api/admin/tournaments/{tournamentId}")
    suspend fun cancelTournament(
        @Path("tournamentId") tournamentId: Long
    ): Response<ResponseBody>

    @POST("api/admin/pairings/{pairingId}/result")
    suspend fun submitPairingResult(
        @Path("pairingId") pairingId: Long,
        @Body result: Map<String, String>
    ): Response<ResponseBody>
}
