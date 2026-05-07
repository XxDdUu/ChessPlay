package com.sky.chessplay.di

import com.sky.chessplay.data.socket.ChessSocketClient
import com.sky.chessplay.domain.socket.ChessSocket
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SocketModule {

    @Provides
    @Singleton
    fun provideChessSocket(): ChessSocket {
        return ChessSocketClient()
    }
}
