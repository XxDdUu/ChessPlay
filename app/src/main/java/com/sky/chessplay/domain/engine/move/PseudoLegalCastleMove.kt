package model.service.move

import com.sky.chessplay.domain.model.File
import com.sky.chessplay.domain.model.KingSideCastle
import com.sky.chessplay.domain.model.Move
import com.sky.chessplay.domain.model.Position
import com.sky.chessplay.domain.model.QueenSideCastle
import model.board.King
import model.board.Rook
import model.state.GameState

fun King.pseudoLegalCastleMoves(gameState: GameState) = listOfNotNull(
    gameState.castle(king = this, castleSide = CastleSide.KingSide),
    gameState.castle(king = this, castleSide = CastleSide.QueenSide),
)

private enum class CastleSide(
    val rookStartingFile: File,
    val emptyFiles: Set<File>,
    val kingPathFiles: Set<File>,
) {
    KingSide(
        rookStartingFile = File.h,
        emptyFiles = setOf(File.f, File.g),
        kingPathFiles = setOf(File.e, File.f, File.g),
    ),
    QueenSide(
        rookStartingFile = File.a,
        emptyFiles = setOf(File.b, File.c, File.d),
        kingPathFiles = setOf(File.e, File.d, File.c),
    ),
}

private fun GameState.castle(king: King, castleSide: CastleSide): Move? {
    val initialRookPosition = Position.fromFileAndRank(castleSide.rookStartingFile, king.startingRank)
    val rook = piecesByPosition[initialRookPosition] ?: return null
    if (rook !is Rook && rook.side == king.side) return null

    val kingMoved = boardSnapshots.any { it.move?.piece === king }
    val rookMoved = boardSnapshots.any { it.move?.piece === rook }
    if (kingMoved || rookMoved) return null

    val pathIsBlocked = castleSide.emptyFiles
        .map { Position.fromFileAndRank(it, king.startingRank) }
        .any { it in piecesByPosition }
    if (pathIsBlocked) return null

    val pathOfKing = castleSide.kingPathFiles.mapTo(mutableSetOf()) { Position.fromFileAndRank(it, king.startingRank) }

    val squaresUnderAttack = piecesByPosition.values
        .filter { it.side == king.side.opposite }
        .flatMap { it.pseudoLegalMoves(this) }
        .any { it.to in pathOfKing }

    if (squaresUnderAttack) return null

    return when (castleSide) {
        CastleSide.KingSide -> KingSideCastle(king)
        CastleSide.QueenSide -> QueenSideCastle(king)
    }
}
