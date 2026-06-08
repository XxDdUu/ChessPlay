package com.sky.chessplay.data.remote.dto.request

data class VerifyOtpRequest(
    val email: String,
    val otp: String
)
