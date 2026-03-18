package model.board

import com.sky.chessplay.domain.model.Rank
import com.sky.chessplay.domain.model.Side
import com.sky.chessplay.domain.model.Side.BLACK
import com.sky.chessplay.domain.model.Side.WHITE

sealed interface Piece {
    val side: Side
    val symbol: String
    val textSymbol: String
    val value: Int
    val asset: String
}

class King(override val side: Side) : Piece {
    override val symbol = when (side) {
        WHITE -> "♔"
        BLACK -> "♚"
    }
    override val textSymbol = "K"
    override val value = 0
    override val asset = "chess_king_${side.name.lowercase()}.svg"

    val startingRank = when (side) {

        WHITE -> Rank.r1
        BLACK -> Rank.r8
    }
}

class Queen(override val side: Side) : Piece {
    override val symbol = when (side) {
        WHITE -> "♕"
        BLACK -> "♛"
    }
    override val textSymbol = "Q"
    override val value = 9
    override val asset = "chess_queen_${side.name.lowercase()}.svg"
}

class Rook(override val side: Side) : Piece {
    override val symbol = when (side) {
        WHITE -> "♖"
        BLACK -> "♜"
    }
    override val textSymbol = "R"
    override val value = 5
    override val asset = "chess_rook_${side.name.lowercase()}.svg"
}

class Bishop(override val side: Side) : Piece {
    override val symbol = when (side) {
        WHITE -> "♗"
        BLACK -> "♝"
    }
    override val textSymbol = "B"
    override val value = 3
    override val asset = "chess_bishop_${side.name.lowercase()}.svg"
}

class Knight(override val side: Side) : Piece {
    override val symbol = when (side) {
        WHITE -> "♘"
        BLACK -> "♞"
    }
    override val textSymbol = "N"
    override val value = 3
    override val asset = "chess_knight_${side.name.lowercase()}.svg"
}

class Pawn(override val side: Side) : Piece {
    override val symbol = when (side) {
        WHITE -> "♙"
        BLACK -> "♟︎"
    }
    override val textSymbol = ""
    override val value = 1
    override val asset = "chess_pawn_${side.name.lowercase()}.svg"

    val startingRank = when (side) {
        WHITE -> Rank.r2
        BLACK -> Rank.r7
    }

    val rankAfterDoubleMove = when (side) {
        WHITE -> Rank.r4
        BLACK -> Rank.r5
    }
}
