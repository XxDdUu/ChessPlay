package com.sky.chessplay.data.remote.dto.response

data class UserAdminResponse(
    val userId: Long,
    val username: String,
    val email: String,
    val role: String,
    val isBanned: Boolean,
    val countryCode: String,
    val rating: Int,
    val createdAt: String
)
