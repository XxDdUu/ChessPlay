package com.sky.chessplay.data.remote.dto.response

data class AiModelsResponse(
    val models: List<AiModelInfo>?,
    val default: String?
)

data class AiModelInfo(
    val key: String,
    val display: String?
)
