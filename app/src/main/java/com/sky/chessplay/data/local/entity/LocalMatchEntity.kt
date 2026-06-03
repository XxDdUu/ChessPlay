package com.sky.chessplay.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_matches")
data class LocalMatchEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val result: String,
    val reason: String,
    val playedAt: Long,
    val moveHistory: String
)