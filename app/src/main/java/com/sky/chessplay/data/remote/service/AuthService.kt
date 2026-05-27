package com.sky.chessplay.data.remote.service

import com.sky.chessplay.data.remote.api.AuthApi
import com.sky.chessplay.data.remote.dto.request.GoogleLoginRequest
import com.sky.chessplay.data.remote.dto.request.LoginRequest
import com.sky.chessplay.data.remote.dto.response.UserResponse
import com.sky.chessplay.remote.dto.request.RegisterRequest
import javax.inject.Inject

class AuthService @Inject constructor(
    private val api: AuthApi
) {
    suspend fun register(userData: RegisterRequest): UserResponse {
        return api.register(userData)
    }

    suspend fun login(userData: LoginRequest): UserResponse {
        return api.login(userData)
    }

    suspend fun getMe(): UserResponse {
        return api.me()
    }
    suspend fun googleLogin(userData: GoogleLoginRequest): UserResponse {
        return api.googleLogin(userData)
    }
}