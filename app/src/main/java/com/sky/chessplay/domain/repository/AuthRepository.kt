package com.sky.chessplay.domain.repository

import com.sky.chessplay.data.remote.dto.request.LoginRequest
import com.sky.chessplay.domain.model.auth.User
import com.sky.chessplay.remote.dto.request.RegisterRequest


interface AuthRepository {
    suspend fun register(request: RegisterRequest): User
    suspend fun login(request: LoginRequest): User
    suspend fun getMe(): User
}
