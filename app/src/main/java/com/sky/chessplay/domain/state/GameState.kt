package model.state

import com.sky.chessplay.domain.model.Board
import com.sky.chessplay.domain.model.BoardSnapshot
import com.sky.chessplay.domain.model.File
import com.sky.chessplay.domain.model.Move
import com.sky.chessplay.domain.model.Position
import com.sky.chessplay.domain.model.Promotion
import com.sky.chessplay.domain.model.Rank
import com.sky.chessplay.domain.model.Side.BLACK
import com.sky.chessplay.domain.model.Side.WHITE
import model.board.Bishop
import model.board.King
import model.board.Knight
import model.board.Pawn
import model.board.Piece
import model.board.Queen
import model.board.Rook

data class GameState(
    val activePosition: Position? = null,
    val legalMoves: List<Move> = emptyList(),
    val boardSnapshots: List<BoardSnapshot> = listOf(
        BoardSnapshot(
            piecesByPosition = initialPieces,
            sideToPlay = WHITE,
        )
    ),
    val promotionSelection: List<Promotion> = emptyList(),
) {
    val piecesByPosition = boardSnapshots.last().piecesByPosition
    val sideToPlay = boardSnapshots.last().sideToPlay
    companion object {
        fun fromFen(fen: String): GameState {

            val parts = fen.split(" ")
            val boardPart = parts[0]
            val turnPart = parts[1]

            val rows = boardPart.split("/")
            val newBoard = mutableMapOf<Position, Piece>()

            for (rankIndex in rows.indices) {
                var fileIndex = 0

                for (char in rows[rankIndex]) {
                    if (char.isDigit()) {
                        fileIndex += char.digitToInt()
                    } else {
                        val file = File.entries[fileIndex]
                        val rank = Rank.entries[7 - rankIndex]

                        val position = Position.fromFileAndRank(file, rank)

                        val piece = when (char) {
                            'p' -> Pawn(BLACK)
                            'r' -> Rook(BLACK)
                            'n' -> Knight(BLACK)
                            'b' -> Bishop(BLACK)
                            'q' -> Queen(BLACK)
                            'k' -> King(BLACK)

                            'P' -> Pawn(WHITE)
                            'R' -> Rook(WHITE)
                            'N' -> Knight(WHITE)
                            'B' -> Bishop(WHITE)
                            'Q' -> Queen(WHITE)
                            'K' -> King(WHITE)

                            else -> null
                        }

                        piece?.let { newBoard[position] = it }
                        fileIndex++
                    }
                }
            }

            val side = if (turnPart == "w") WHITE else BLACK

            return GameState(
                boardSnapshots = listOf(
                    BoardSnapshot(
                        piecesByPosition = newBoard,
                        sideToPlay = side
                    )
                )
            )
        }
    }
}

private val initialPieces: Board = mapOf(
    Position.a8 to Rook(BLACK),
    Position.b8 to Knight(BLACK),
    Position.c8 to Bishop(BLACK),
    Position.d8 to Queen(BLACK),
    Position.e8 to King(BLACK),
    Position.f8 to Bishop(BLACK),
    Position.g8 to Knight(BLACK),
    Position.h8 to Rook(BLACK),

    Position.a7 to Pawn(BLACK),
    Position.b7 to Pawn(BLACK),
    Position.c7 to Pawn(BLACK),
    Position.d7 to Pawn(BLACK),
    Position.e7 to Pawn(BLACK),
    Position.f7 to Pawn(BLACK),
    Position.g7 to Pawn(BLACK),
    Position.h7 to Pawn(BLACK),

    Position.a2 to Pawn(WHITE),
    Position.b2 to Pawn(WHITE),
    Position.c2 to Pawn(WHITE),
    Position.d2 to Pawn(WHITE),
    Position.e2 to Pawn(WHITE),
    Position.f2 to Pawn(WHITE),
    Position.g2 to Pawn(WHITE),
    Position.h2 to Pawn(WHITE),

    Position.a1 to Rook(WHITE),
    Position.b1 to Knight(WHITE),
    Position.c1 to Bishop(WHITE),
    Position.d1 to Queen(WHITE),
    Position.e1 to King(WHITE),
    Position.f1 to Bishop(WHITE),
    Position.g1 to Knight(WHITE),
    Position.h1 to Rook(WHITE),
)

