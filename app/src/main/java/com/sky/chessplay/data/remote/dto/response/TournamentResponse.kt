package com.sky.chessplay.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class TournamentResponse(
    @SerializedName("tournamentId")
    val id: Long?,

    @SerializedName("tournamentName")
    val name: String?,

    val description: String?,
    val totalRounds: Int?,
    val timeControl: String?,
    val registrationStart: String?,
    val registrationEnd: String?,
    val startTime: String?,
    val status: String?,

    @SerializedName("createdById")
    val creatorId: Long?,

    @SerializedName("createdByName")
    val creatorName: String?
)