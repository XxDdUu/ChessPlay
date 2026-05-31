package com.sky.chessplay.domain.engine.util

fun parsePgnToMoves(pgn: String): List<String> {
    if (pgn.isBlank()) return emptyList()

    val tokens = pgn.trim().split("\\s+".toRegex())

    return tokens.filter { token -> !token.contains(".") && token.isNotBlank() }
}