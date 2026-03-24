package com.sky.chessplay.remote.dto.request

data class RegisterRequest(
    val password: String,
    val email: String,
    val username: String,
    val country_code: String,
)
