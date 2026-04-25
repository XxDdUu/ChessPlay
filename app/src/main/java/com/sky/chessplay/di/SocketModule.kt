package com.sky.chessplay.di

import com.sky.chessplay.data.socket.ChessSocketClient
import com.sky.chessplay.domain.socket.ChessSocket
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SocketModule {

    @Provides
    fun provideChessSocket(): ChessSocket {
        return ChessSocketClient()
    }
}
