package com.sky.chessplay.domain.model.auth

data class User(
    val id: Long,
    val username: String,
    val country_code: String,
    val createdAt: String,
)