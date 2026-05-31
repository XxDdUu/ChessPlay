package com.sky.chessplay.domain.engine.util

import com.sky.chessplay.domain.model.chess.Board
import com.sky.chessplay.domain.model.chess.File
import com.sky.chessplay.domain.model.chess.Position
import com.sky.chessplay.domain.model.chess.Rank
import com.sky.chessplay.domain.model.chess.Side
import model.board.Bishop
import model.board.King
import model.board.Knight
import model.board.Pawn
import model.board.Queen
import model.board.Rook

fun boardToFen(board: Board, sideToPlay: String = "w"): String {
    val fenRows = mutableListOf<String>()

    for (rankIndex in 7 downTo 0) {
        val rank = Rank.entries[rankIndex]
        var emptyCount = 0
        val rowString = StringBuilder()

        for (fileIndex in 0..7) {
            val file = File.entries[fileIndex]
            val position = Position.fromFileAndRank(file, rank)
            val piece = board[position]

            if (piece == null) {
                emptyCount++
            } else {
                if (emptyCount > 0) {
                    rowString.append(emptyCount)
                    emptyCount = 0
                }
                val char = when (piece) {
                    is Pawn -> 'p'
                    is Rook -> 'r'
                    is Knight -> 'n'
                    is Bishop -> 'b'
                    is Queen -> 'q'
                    is King -> 'k'
                    else -> 'p'
                }
                rowString.append(if (piece.side == Side.WHITE) char.uppercaseChar() else char)
            }
        }
        if (emptyCount > 0) {
            rowString.append(emptyCount)
        }
        fenRows.add(rowString.toString())
    }

    return fenRows.joinToString("/") + " $sideToPlay KQkq - 0 1"
}