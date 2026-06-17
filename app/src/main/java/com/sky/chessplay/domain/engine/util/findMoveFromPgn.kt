package com.sky.chessplay.domain.engine.util

import com.sky.chessplay.domain.model.chess.Board
import com.sky.chessplay.domain.model.chess.Capture
import com.sky.chessplay.domain.model.chess.CapturePromotion
import com.sky.chessplay.domain.model.chess.EnPassant
import com.sky.chessplay.domain.model.chess.File
import com.sky.chessplay.domain.model.chess.KingSideCastle
import com.sky.chessplay.domain.model.chess.Move
import com.sky.chessplay.domain.model.chess.Position
import com.sky.chessplay.domain.model.chess.QueenSideCastle
import com.sky.chessplay.domain.model.chess.Rank
import com.sky.chessplay.domain.model.chess.Side
import com.sky.chessplay.domain.model.chess.StandardMove
import com.sky.chessplay.domain.model.chess.StandardPromotion
import model.board.Bishop
import model.board.King
import model.board.Knight
import model.board.Pawn
import model.board.Queen
import model.board.Rook
import kotlin.math.abs

fun findMoveFromPgn(board: Board, pgnMove: String, isWhiteTurn: Boolean): Move? {
    val currentSide = if (isWhiteTurn) Side.WHITE else Side.BLACK
    var cleanMove = pgnMove
        .trim()
        .replace("+", "")
        .replace("#", "")
        .replace("!", "")
        .replace("?", "")

    findMoveFromUci(board, cleanMove, currentSide)?.let { return it }

    if (cleanMove == "O-O") return KingSideCastle(King(currentSide))
    if (cleanMove == "O-O-O") return QueenSideCastle(King(currentSide))

    var promoPieceChar: Char? = null
    if (cleanMove.endsWith("Q") || cleanMove.endsWith("R") || cleanMove.endsWith("B") || cleanMove.endsWith("N")) {
        promoPieceChar = cleanMove.last()
        cleanMove = if (cleanMove.contains("=")) {
            cleanMove.substringBefore("=")
        } else {
            cleanMove.dropLast(1)
        }
    }

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

            val fileDelta = abs(position.file.ordinal - toFile.ordinal)
            val rankDelta = abs(position.rank.ordinal - toRank.ordinal)

            when (targetPieceClass) {
                Knight::class -> {
                    val isLMove = (fileDelta == 1 && rankDelta == 2) || (fileDelta == 2 && rankDelta == 1)
                    if (!isLMove) continue
                }

                Pawn::class -> {
                    if (cleanMove.contains("x")) {
                        if (fileDelta != 1 || rankDelta != 1) continue
                    } else {
                        if (position.file != toFile) continue // Đi thẳng bắt buộc cùng cột
                    }
                }

                Bishop::class -> {
                    if (fileDelta != rankDelta) continue
                }

                Rook::class -> {
                    if (fileDelta != 0 && rankDelta != 0) continue
                }

                Queen::class -> {
                    val isStraight = (fileDelta == 0 || rankDelta == 0)
                    val isDiagonal = (fileDelta == rankDelta)
                    if (!isStraight && !isDiagonal) continue
                }
            }

            val isCapture = cleanMove.contains("x") || board.containsKey(toPosition)
            val promoPiece = when (promoPieceChar) {
                'Q' -> Queen(currentSide)
                'R' -> Rook(currentSide)
                'B' -> Bishop(currentSide)
                'N' -> Knight(currentSide)
                else -> Queen(currentSide)
            }
            return if (isCapture) {
                val captured = board[toPosition] ?: Pawn(if (isWhiteTurn) Side.BLACK else Side.WHITE)
                if (promoPieceChar != null && piece is Pawn) {
                    CapturePromotion(piece, position, toPosition, promoPiece, captured)
                } else {
                    Capture(piece, position, toPosition, captured)
                }
            } else {
                if (promoPieceChar != null && piece is Pawn) {
                    StandardPromotion(piece, position, toPosition, promoPiece)
                } else {
                    StandardMove(piece, position, toPosition)
                }
            }
        }
    }
    return null
}

private fun findMoveFromUci(board: Board, move: String, currentSide: Side): Move? {
    val normalizedMove = move.lowercase()
    if (!normalizedMove.matches(Regex("[a-h][1-8][a-h][1-8][qrbn]?"))) return null

    val fromPosition = positionFromCoordinate(normalizedMove.substring(0, 2)) ?: return null
    val toPosition = positionFromCoordinate(normalizedMove.substring(2, 4)) ?: return null
    val piece = board[fromPosition]?.takeIf { it.side == currentSide } ?: return null
    val capturedPiece = board[toPosition]
    val promotionPiece = normalizedMove.getOrNull(4)?.let { promotionPieceFor(it, currentSide) }

    if (piece is King && fromPosition.file == File.e) {
        if (toPosition.file == File.g) return KingSideCastle(piece)
        if (toPosition.file == File.c) return QueenSideCastle(piece)
    }

    if (promotionPiece != null && piece is Pawn) {
        return if (capturedPiece != null) {
            CapturePromotion(piece, fromPosition, toPosition, promotionPiece, capturedPiece)
        } else {
            StandardPromotion(piece, fromPosition, toPosition, promotionPiece)
        }
    }

    if (capturedPiece != null) {
        return Capture(piece, fromPosition, toPosition, capturedPiece)
    }

    val enPassantCapturePosition = Position.fromFileAndRank(toPosition.file, fromPosition.rank)
    val enPassantCapturedPiece = board[enPassantCapturePosition]
    if (piece is Pawn &&
        fromPosition.file != toPosition.file &&
        enPassantCapturedPiece is Pawn &&
        enPassantCapturedPiece.side != currentSide
    ) {
        return EnPassant(piece, fromPosition, toPosition, enPassantCapturedPiece, enPassantCapturePosition)
    }

    return StandardMove(piece, fromPosition, toPosition)
}

private fun positionFromCoordinate(coordinate: String): Position? {
    val file = File.entries.find { it.name == coordinate[0].toString() } ?: return null
    val rank = Rank.entries.find { it.number == coordinate[1].digitToInt() } ?: return null
    return Position.fromFileAndRank(file, rank)
}

private fun promotionPieceFor(piece: Char, side: Side) = when (piece) {
    'q' -> Queen(side)
    'r' -> Rook(side)
    'b' -> Bishop(side)
    'n' -> Knight(side)
    else -> null
}
