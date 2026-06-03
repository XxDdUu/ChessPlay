package com.sky.chessplay.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sky.chessplay.data.local.entity.LocalMatchEntity

@Dao
interface LocalMatchDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: LocalMatchEntity)

    @Query("SELECT * FROM local_matches ORDER BY playedAt DESC")
    suspend fun getAllMatches(): List<LocalMatchEntity>

    @Query("DELETE FROM local_matches WHERE id = :id")
    suspend fun deleteMatch(id: Long)
}
