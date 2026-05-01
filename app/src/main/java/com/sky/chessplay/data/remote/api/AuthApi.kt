package com.sky.chessplay.data.remote.api

import com.sky.chessplay.data.remote.dto.request.GoogleLoginRequest
import com.sky.chessplay.data.remote.dto.request.LoginRequest
import com.sky.chessplay.data.remote.dto.response.UserResponse
import com.sky.chessplay.remote.dto.request.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST


interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(@Body req: LoginRequest): UserResponse
    @POST("api/auth/signup")
    suspend fun  register(@Body req: RegisterRequest): UserResponse
    @GET("api/auth/me")
    suspend fun me(@Header("Authorization") token: String
    ): UserResponse
    @POST("api/auth/google")
    suspend fun googleLogin(
        @Body request: GoogleLoginRequest
    ): UserResponse
}
