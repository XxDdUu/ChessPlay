package com.sky.chessplay.data.remote.api

import com.sky.chessplay.data.remote.dto.response.UserProfileResponse
import retrofit2.http.GET

interface ProfileApi {

    @GET("api/user/me")
    suspend fun getProfile(): UserProfileResponse
}
