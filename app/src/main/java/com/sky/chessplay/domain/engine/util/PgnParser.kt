package com.sky.chessplay.domain.engine.util

fun parsePgnToMoves(pgn: String): List<String> {
    if (pgn.isBlank()) return emptyList()

    // Safely remove any curly braces comments/annotations like {AI:best_model} before splitting
    val cleanPgn = pgn.replace(Regex("\\{.*?\\}"), "").trim()
    if (cleanPgn.isBlank()) return emptyList()

    val tokens = cleanPgn.split("\\s+".toRegex())

    return tokens.filter { token -> !token.contains(".") && token.isNotBlank() }
}