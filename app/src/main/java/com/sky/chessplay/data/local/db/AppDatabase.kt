package com.sky.chessplay.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sky.chessplay.data.local.entity.LocalMatchEntity

@Database(
    entities = [LocalMatchEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun localMatchDao(): LocalMatchDao
}