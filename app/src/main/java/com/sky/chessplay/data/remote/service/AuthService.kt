package com.sky.chessplay.data.remote.service

import android.util.Log
import com.sky.chessplay.data.local.datastore.TokenManager
import com.sky.chessplay.data.remote.api.AuthApi
import com.sky.chessplay.data.remote.dto.request.LoginRequest
import com.sky.chessplay.data.remote.dto.response.UserResponse
import com.sky.chessplay.remote.dto.request.RegisterRequest
import javax.inject.Inject

class AuthService @Inject constructor(
    private val api: AuthApi,
    private val tokenManager: TokenManager
) {
    suspend fun register(
        userData: RegisterRequest
    ): UserResponse {
        try {
            return api.register(userData)
        } catch (e: Exception) {
            Log.e("AuthService", "Registration error", e)
            throw e
        }
    }
    suspend fun login(
        userData: LoginRequest
    ): UserResponse {
       try {
            val response = api.login(userData)
           tokenManager.saveToken(response.token)
           return response
       } catch (e: Exception) {
           Log.e("AuthService", "Login error", e)
           throw e
       }
    }

     suspend fun getMe(): UserResponse {
         try {
             val token = tokenManager.getToken()
             return api.me("Bearer $token")
         } catch (e: Exception) {
             Log.e("AuthService", "Get me error", e)
             throw e
         }
    }
}