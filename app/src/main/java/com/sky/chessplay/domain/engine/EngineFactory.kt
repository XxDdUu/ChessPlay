package com.sky.chessplay.domain.engine

import com.sky.chessplay.di.OfflineEngine
import com.sky.chessplay.di.OnlineEngine
import javax.inject.Inject

class EngineFactory @Inject constructor(
    @OnlineEngine private val onlineEngine: ChessEngine,
    @OfflineEngine private val offlineEngine: ChessEngine
) {

    fun create(isOnline: Boolean): ChessEngine {
        return if (isOnline) onlineEngine else offlineEngine
    }
}