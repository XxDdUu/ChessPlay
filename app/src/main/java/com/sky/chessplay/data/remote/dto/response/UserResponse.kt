package com.sky.chessplay.data.remote.dto.response

data class UserResponse (
    val id: Long,
    val username: String,
    val country_code: String,
    val createdAt: String,
    val token: String?
)