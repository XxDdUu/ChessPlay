package com.sky.chessplay.domain.socket

import kotlinx.coroutines.flow.SharedFlow

interface MatchSocket {
    val events: SharedFlow<MatchEvent>
    fun connect(token: String)
    fun disconnect()
    fun joinMatchmaking(userId: Long)
    fun observeEvents(listener: (MatchEvent) -> Unit)
}
