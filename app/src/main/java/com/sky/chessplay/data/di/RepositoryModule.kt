package com.sky.chessplay.di

import com.sky.chessplay.domain.repository.AuthRepository
import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import zh.qingzi.portaljob.data.repository.AuthRepositoryImpl;

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository
}
