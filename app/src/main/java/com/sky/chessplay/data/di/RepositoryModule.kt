package com.sky.chessplay.di

import com.sky.chessplay.data.repository.FriendRepositoryImpl
import com.sky.chessplay.data.repository.MatchRepositoryImpl
import com.sky.chessplay.domain.repository.AuthRepository
import com.sky.chessplay.domain.repository.FriendRepository
import com.sky.chessplay.domain.repository.MatchRepository
import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import zh.qingzi.portaljob.data.repository.AuthRepositoryImpl;
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository
    @Binds
    @Singleton
    abstract fun bindMatchRepository(
        impl: MatchRepositoryImpl
    ): MatchRepository
    @Binds
    @Singleton
    abstract fun bindFriendRepository(
        impl: FriendRepositoryImpl
    ): FriendRepository
}
