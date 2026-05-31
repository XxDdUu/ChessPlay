package com.sky.chessplay.domain.engine.util

import com.sky.chessplay.domain.model.chess.Board
import com.sky.chessplay.domain.model.chess.Capture
import com.sky.chessplay.domain.model.chess.File
import com.sky.chessplay.domain.model.chess.KingSideCastle
import com.sky.chessplay.domain.model.chess.Move
import com.sky.chessplay.domain.model.chess.Position
import com.sky.chessplay.domain.model.chess.QueenSideCastle
import com.sky.chessplay.domain.model.chess.Rank
import com.sky.chessplay.domain.model.chess.Side
import com.sky.chessplay.domain.model.chess.StandardMove
import model.board.Bishop
import model.board.King
import model.board.Knight
import model.board.Pawn
import model.board.Queen
import model.board.Rook

fun findMoveFromPgn(board: Board, pgnMove: String, isWhiteTurn: Boolean): Move? {
    val cleanMove = pgnMove.replace("+", "").replace("#", "")
    val currentSide = if (isWhiteTurn) Side.WHITE else Side.BLACK

    if (cleanMove == "O-O") return KingSideCastle(King(currentSide))
    if (cleanMove == "O-O-O") return QueenSideCastle(King(currentSide))

    val toFileChar = cleanMove.takeLast(2).first()
    val toRankChar = cleanMove.last()
    val toFile = File.entries.find { it.name == toFileChar.toString() } ?: return null
    val toRank = Rank.entries.find { it.number == toRankChar.digitToInt() } ?: return null
    val toPosition = Position.fromFileAndRank(toFile, toRank)

    val firstChar = cleanMove.first()
    val targetPieceClass = when {
        firstChar.isUpperCase() -> when (firstChar) {
            'N' -> Knight::class
            'B' -> Bishop::class
            'R' -> Rook::class
            'Q' -> Queen::class
            'K' -> King::class
            else -> Pawn::class
        }
        else -> Pawn::class
    }

    var sourceFileConstraint: File? = null
    var sourceRankConstraint: Rank? = null

    if (targetPieceClass == Pawn::class) {
        if (cleanMove.contains("x")) {
            sourceFileConstraint = File.entries.find { it.name == firstChar.toString() }
        }
    } else {
        // Đối với quân lớn (ví dụ: Nfd2, R1e7), kiểm tra các ký tự ở giữa
        val remainder = cleanMove.drop(1).dropLast(2).replace("x", "")
        if (remainder.isNotEmpty()) {
            val charConstraint = remainder.first()
            if (charConstraint.isDigit()) {
                sourceRankConstraint = Rank.entries.find { it.number == charConstraint.digitToInt() }
            } else {
                sourceFileConstraint = File.entries.find { it.name == charConstraint.toString() }
            }
        }
    }

    for ((position, piece) in board) {
        if (piece.side == currentSide && targetPieceClass.isInstance(piece)) {

            if (sourceFileConstraint != null && position.file != sourceFileConstraint) continue
            if (sourceRankConstraint != null && position.rank != sourceRankConstraint) continue

            if (targetPieceClass == Pawn::class && !cleanMove.contains("x") && position.file != toFile) {
                continue
            }

            val isCapture = cleanMove.contains("x") || board.containsKey(toPosition)
            return if (isCapture) {
                val captured = board[toPosition] ?: Pawn(if (isWhiteTurn) Side.BLACK else Side.WHITE)
                Capture(piece, position, toPosition, captured)
            } else {
                StandardMove(piece, position, toPosition)
            }
        }
    }
    return null
}