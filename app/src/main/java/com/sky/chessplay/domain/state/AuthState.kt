package com.sky.chessplay.domain.state

import com.sky.chessplay.domain.model.auth.User

sealed class AuthState {

    object Idle : AuthState()

    object Loading : AuthState()

    object Unauthenticated : AuthState()
    data class Authenticated(
        val user: User
    ) : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}
