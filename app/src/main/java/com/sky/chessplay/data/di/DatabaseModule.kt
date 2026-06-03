package com.sky.chessplay.data.di

import android.content.Context
import androidx.room.Room
import com.sky.chessplay.data.local.db.AppDatabase
import com.sky.chessplay.data.local.db.LocalMatchDao
import com.sky.chessplay.data.repository.LocalMatchRepositoryImpl
import com.sky.chessplay.domain.repository.LocalMatchRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseModule {

    @Binds
    @Singleton
    abstract fun bindLocalMatchRepository(
        impl: LocalMatchRepositoryImpl
    ): LocalMatchRepository

    companion object {
        @Provides
        @Singleton
        fun provideAppDatabase(
            @ApplicationContext context: Context
        ): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "chessplay_db"
            ).build()
        }

        @Provides
        @Singleton
        fun provideLocalMatchDao(
            db: AppDatabase
        ): LocalMatchDao {
            return db.localMatchDao()
        }
    }
}
