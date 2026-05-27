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
    @POST("api/auth/register")
    suspend fun  register(@Body req: RegisterRequest): UserResponse
    @GET("api/auth/me")
    suspend fun me(): UserResponse
    @POST("api/auth/google")
    suspend fun googleLogin(
        @Body request: GoogleLoginRequest
    ): UserResponse
}
