package com.sky.chessplay.data.remote.api

import com.sky.chessplay.data.remote.dto.response.FriendResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface FriendApi {

    @GET("api/friends/list")
    suspend fun getFriends(
        @Query("userId") userId: Long
    ): Response<List<FriendResponse>>

    @GET("api/friends/pending")
    suspend fun getPending(
        @Query("userId") userId: Long
    ): Response<List<FriendResponse>>

    @POST("api/friends/request")
    suspend fun sendRequest(
        @Query("senderId") senderId: Long,
        @Query("receiverId") receiverId: Long
    ): Response<String>

    @POST("api/friends/accept")
    suspend fun acceptRequest(
        @Query("user1") user1: Long,
        @Query("user2") user2: Long
    ): Response<String>
    @POST("api/friends/remove")
    suspend fun removeFriend(
        @Query("user1") user1: Long,
        @Query("user2") user2: Long
    ): Response<String>
}