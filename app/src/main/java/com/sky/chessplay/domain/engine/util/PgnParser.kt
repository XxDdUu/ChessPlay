package com.sky.chessplay.domain.engine.util

fun parsePgnToMoves(pgn: String): List<String> {
    if (pgn.isBlank()) return emptyList()

    // Safely remove any curly braces comments/annotations like {AI:best_model} before splitting
    val cleanPgn = pgn
        .replace(Regex("\\{.*?\\}"), "")
        .replace(Regex("\\[.*?\\]"), "")
        .trim()
    if (cleanPgn.isBlank()) return emptyList()

    val tokens = cleanPgn.split("\\s+".toRegex())
    val resultTokens = setOf("1-0", "0-1", "1/2-1/2", "*")

    return tokens.filter { token ->
        token.isNotBlank() &&
            !token.contains(".") &&
            token !in resultTokens
    }
}
