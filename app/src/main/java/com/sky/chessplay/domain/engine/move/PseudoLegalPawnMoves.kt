package model.service.move

import com.sky.chessplay.domain.model.BoardSnapshot
import com.sky.chessplay.domain.model.Capture
import com.sky.chessplay.domain.model.CapturePromotion
import com.sky.chessplay.domain.model.CapturingMove
import com.sky.chessplay.domain.model.EnPassant
import com.sky.chessplay.domain.model.Move
import com.sky.chessplay.domain.model.NonCapturingMove
import com.sky.chessplay.domain.model.Position
import com.sky.chessplay.domain.model.Rank
import com.sky.chessplay.domain.model.Side
import com.sky.chessplay.domain.model.StandardMove
import com.sky.chessplay.domain.model.StandardPromotion
import model.board.Bishop
import model.board.Knight
import model.board.Pawn
import model.board.Queen
import model.board.Rook
import model.service.util.positionOf
import model.state.GameState


private const val Left = -1
private const val Right = 1
private const val Up = 1
private const val Down = -1

fun Pawn.pseudoLegalPawnMoves(gameState: GameState): List<Move> {
    val from = gameState positionOf this

    val moveInfo = MoveInfo(
        pawn = this,
        from = from,
        gameState = gameState,
    )

    return listOfNotNull(
        moveInfo.oneStepForward(),
        moveInfo.twoStepForward(),
        moveInfo.capture(Left),
        moveInfo.capture(Right),
        moveInfo.enPassantCapture(Left),
        moveInfo.enPassantCapture(Right),
    ).flatMap { it.mapToPromotionIfPossible() }
}

private data class MoveInfo(
    val pawn: Pawn,
    val from: Position,
    val gameState: GameState,
) {
    val direction = when (pawn.side) {
        Side.WHITE -> Up
        Side.BLACK -> Down
    }
}

private fun MoveInfo.oneStepForward(): Move? {
    val to = from + (0 to direction * 1)
    val pawnOnDestination = gameState.piecesByPosition[to]
    if (to == null || pawnOnDestination != null) {
        return null
    }

    return StandardMove(piece = pawn, from = from, to = to)
}

private fun MoveInfo.twoStepForward(): Move? {
    val moveOver = from + (0 to direction * 1)
    val to = from + (0 to direction * 2)
    val pawnOnMoveOver = gameState.piecesByPosition[moveOver]
    val pawnOnDestination = gameState.piecesByPosition[to]

    if (to == null || pawnOnMoveOver != null || pawnOnDestination != null || from.rank != pawn.startingRank) {
        return null
    }

    return StandardMove(piece = pawn, from = from, to = to)
}

private fun MoveInfo.capture(side: Int): Move? {
    val to = from + (side to direction * 1)
    val capturedPiece = gameState.piecesByPosition[to]
    if (to == null || capturedPiece == null || capturedPiece.side == pawn.side) {
        return null
    }
    return Capture(piece = pawn, from = from, to = to, capturedPiece = capturedPiece)
}

private fun MoveInfo.enPassantCapture(side: Int): Move? {
    val to = from + (side to direction * 1)
    val enPassant = from + (side to 0)
    val pieceOnTo = gameState.piecesByPosition[to]
    val capturedPiece = gameState.piecesByPosition[enPassant]


    if (to == null || enPassant == null || capturedPiece == null || capturedPiece.side == pawn.side || pieceOnTo != null) {
        return null
    }

    val previousMove = gameState.boardSnapshots.previous()?.move
    if (
        previousMove == null ||
        previousMove.piece !== capturedPiece ||
        capturedPiece !is Pawn ||
        previousMove.from.rank != capturedPiece.startingRank ||
        previousMove.to.rank != capturedPiece.rankAfterDoubleMove
    ) {
        return null
    }

    return EnPassant(
        piece = pawn,
        from = from,
        to = to,
        capturedPiece = capturedPiece,
        capturedOn = enPassant
    )
}

private fun List<BoardSnapshot>.previous() = getOrNull(lastIndex - 1)

private val Side.promotionOnRank
    get() = when (this) {
        Side.WHITE -> Rank.r8
        Side.BLACK -> Rank.r1
    }

private fun Move.mapToPromotionIfPossible(): List<Move> =
    if (to.rank == piece.side.promotionOnRank) mapToPromotions()
    else listOf(this)

private fun Move.mapToPromotions() =
    listOf(Queen(piece.side), Knight(piece.side), Rook(piece.side), Bishop(piece.side)).map {
        when (this) {
            is NonCapturingMove -> StandardPromotion(
                piece = piece as Pawn,
                from = from,
                to = to,
                pieceAfterPromotion = it,
            )

            is CapturingMove -> CapturePromotion(
                piece = piece as Pawn,
                from = from,
                to = to,
                pieceAfterPromotion = it,
                capturedPiece = capturedPiece,
            )
        }
    }
