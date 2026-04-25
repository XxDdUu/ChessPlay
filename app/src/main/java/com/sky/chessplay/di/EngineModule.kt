package com.sky.chessplay.di

import com.sky.chessplay.data.engine.RemoteChessEngine
import com.sky.chessplay.domain.engine.ChessEngine
import com.sky.chessplay.domain.engine.LocalChessEngine
import com.sky.chessplay.domain.socket.ChessSocket
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object EngineModule {

    @OnlineEngine
    @Provides
    fun provideRemoteEngine(
        socket: ChessSocket
    ): ChessEngine {
        return RemoteChessEngine(socket)
    }

    @OfflineEngine
    @Provides
    fun provideLocalEngine(): ChessEngine {
        return LocalChessEngine()
    }
}
