package com.sky.chessplay.domain.model.tournament

import com.sky.chessplay.domain.state.TournamentStatus

data class Tournament(
    val id: Long,
    val name: String,
    val description: String,
    val totalRounds: Int,
    val timeControl: String,
    val registrationStart: String,
    val registrationEnd: String,
    val startTime: String,
    val status: TournamentStatus,
    val createdById: Long,
    val createdByName: String
)