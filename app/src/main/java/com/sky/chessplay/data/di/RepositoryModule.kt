package com.sky.chessplay.di

import com.sky.chessplay.data.repository.AdminRepositoryImpl
import com.sky.chessplay.data.repository.AiRepositoryImpl
import com.sky.chessplay.data.repository.FriendRepositoryImpl
import com.sky.chessplay.data.repository.GameRepositoryImpl
import com.sky.chessplay.data.repository.MatchRepositoryImpl
import com.sky.chessplay.data.repository.TournamentRepositoryImpl
import com.sky.chessplay.domain.repository.AdminRepository
import com.sky.chessplay.domain.repository.AiRepository
import com.sky.chessplay.domain.repository.AuthRepository
import com.sky.chessplay.domain.repository.FriendRepository
import com.sky.chessplay.domain.repository.GameRepository
import com.sky.chessplay.domain.repository.MatchRepository
import com.sky.chessplay.domain.repository.TournamentRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import zh.qingzi.portaljob.data.repository.AuthRepositoryImpl
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
    @Binds
    @Singleton
    abstract fun bindAiRepository(
        impl: AiRepositoryImpl
    ): AiRepository
    @Binds
    abstract fun bindGameRepository(
        impl: GameRepositoryImpl
    ): GameRepository
    @Binds
    abstract fun bindTournamentRepository(
        impl: TournamentRepositoryImpl
    ): TournamentRepository
    @Binds
    abstract fun bindAdmintRepository(
        impl: AdminRepositoryImpl
    ): AdminRepository
}

