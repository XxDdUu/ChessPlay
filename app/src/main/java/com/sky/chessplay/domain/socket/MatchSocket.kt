package com.sky.chessplay.domain.socket

interface MatchSocket {
    fun connect(token: String)
    fun disconnect()
    fun joinMatchmaking(userId: Long)
    fun observeEvents(listener: (MatchEvent) -> Unit)
}
