package com.sky.chessplay.domain.model

fun Move.toUci(): String {
    val from = "${from.file.name.lowercase()}${from.rank.ordinal + 1}"
    val to = "${to.file.name.lowercase()}${to.rank.ordinal + 1}"
    return from + to
}
