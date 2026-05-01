package com.sky.chessplay.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("userId")
    val id: Long,

    val username: String,

    @SerializedName("countryCode")
    val country_code: String,

    @SerializedName("createdAt")
    val createdAt: String,

    val avatar: String?,

    val token: String?
)
