package com.sky.chessplay.data.remote.api

import com.sky.chessplay.data.remote.dto.request.AiMoveRequest
import com.sky.chessplay.data.remote.dto.request.AiResignRequest
import com.sky.chessplay.data.remote.dto.request.AiStartRequest
import com.sky.chessplay.data.remote.dto.response.AiGameResponse
import com.sky.chessplay.data.remote.dto.response.AiModelsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AiApi {

    @GET("api/ai/models")
    suspend fun getAvailableModels(): Response<AiModelsResponse>

    @POST("api/ai/start")
    suspend fun startGame(@Body req: AiStartRequest): Response<AiGameResponse>

    @POST("api/ai/move")
    suspend fun makeMove(@Body req: AiMoveRequest): Response<AiGameResponse>

    @POST("api/ai/resign")
    suspend fun resignGame(@Body req: AiResignRequest): Response<AiGameResponse>

    @GET("api/ai/active")
    suspend fun checkActiveGame(): Response<AiGameResponse>

    @GET("api/ai/game/{gameId}")
    suspend fun getGameDetails(@Path("gameId") gameId: String): Response<AiGameResponse>
}
