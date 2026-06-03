package com.sky.chessplay.domain.engine.util

import com.sky.chessplay.domain.model.chess.*
import model.board.*
import model.service.move.pseudoLegalMoves
import model.state.GameState

fun Move.toPgn(gameState: GameState): String {
    if (this is KingSideCastle) return "O-O"
    if (this is QueenSideCastle) return "O-O-O"

    val board = gameState.piecesByPosition
    val pieceSymbol = when (piece) {
        is King -> "K"
        is Queen -> "Q"
        is Rook -> "R"
        is Bishop -> "B"
        is Knight -> "N"
        else -> "" // Pawn
    }

    val isCapture = this is CapturingMove || board.containsKey(to)
    val captureSymbol = if (isCapture) "x" else ""

    if (piece is Pawn) {
        val promoStr = if (this is Promotion) "=${(this as Promotion).pieceAfterPromotion.textSymbol.uppercase()}" else ""
        return if (isCapture) {
            val fromFile = from.file.name.lowercase()
            val toStr = "${to.file.name.lowercase()}${to.rank.ordinal + 1}"
            "${fromFile}x${toStr}${promoStr}"
        } else {
            val toStr = "${to.file.name.lowercase()}${to.rank.ordinal + 1}"
            "${toStr}${promoStr}"
        }
    }

    // For non-pawns, check if we need disambiguation.
    val otherPiecesOfSameType = board.entries.filter { (pos, p) ->
        pos != from && p.side == piece.side && p::class == piece::class
    }

    var disambiguation = ""
    if (otherPiecesOfSameType.isNotEmpty()) {
        val candidates = otherPiecesOfSameType.filter { (_, p) ->
            val pseudoMoves = p.pseudoLegalMoves(gameState, false)
            pseudoMoves.any { it.to == to }
        }

        if (candidates.isNotEmpty()) {
            val shareFile = candidates.any { it.key.file == from.file }
            val shareRank = candidates.any { it.key.rank == from.rank }

            disambiguation = when {
                shareFile && !shareRank -> "${from.rank.ordinal + 1}"
                !shareFile -> from.file.name.lowercase()
                else -> "${from.file.name.lowercase()}${from.rank.ordinal + 1}"
            }
        }
    }

    val toStr = "${to.file.name.lowercase()}${to.rank.ordinal + 1}"
    return "${pieceSymbol}${disambiguation}${captureSymbol}${toStr}"
}
