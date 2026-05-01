package com.sky.chessplay.mapper

import com.sky.chessplay.data.remote.dto.response.UserResponse
import com.sky.chessplay.domain.model.auth.User


fun UserResponse.toDomain(): User = User(
    id = id,
    username = username ?: "Unknown",
    country_code = country_code ?: "VN",
    createdAt = createdAt ?: "",
    avatar = avatar ?: null,
)