package model.service

import androidx.compose.ui.geometry.Offset
import com.sky.chessplay.domain.engine.ChessEngine
import com.sky.chessplay.domain.engine.EngineFactory
import com.sky.chessplay.domain.model.BoardSnapshot
import com.sky.chessplay.domain.model.CapturingMove
import com.sky.chessplay.domain.model.File
import com.sky.chessplay.domain.model.Move
import com.sky.chessplay.domain.model.Position
import com.sky.chessplay.domain.model.Promotion
import com.sky.chessplay.domain.model.Rank
import com.sky.chessplay.ui.state.UiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import model.board.King
import model.board.Piece
import model.service.move.pseudoLegalMoves
import model.state.GameState
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

interface ChessUiService {
    val uiState: UiState
    val gameState: GameState
    fun init(isOnline: Boolean)
    fun onClick(position: Position)
    fun onDragStart(position: Position)
    fun onDrag(offset: Offset)
    fun onDragEnd()
    fun applyPromotion(promotion: Promotion)
    fun cancelPromotion()
    fun onSquareSizeChanged(squareSize: Int)
    fun updateOnStateChanges(onState: GameStateObserver)
}

typealias GameStateObserver = (GameState) -> Unit

class DefaultChessUiService @Inject constructor(
    private val engineFactory: EngineFactory
) : ChessUiService {
    init {
        CoroutineScope(Dispatchers.Main).launch {
            engine.gameStateFlow.collect {
                gameState = it
                update()
            }
        }
    }
    override var uiState = UiState()
        private set

    override var gameState = GameState()
    private val observers = HashSet<GameStateObserver>()

    private lateinit var engine: ChessEngine

    override fun init(isOnline: Boolean) {
        engine = engineFactory.create(isOnline)
    }

    override fun onClick(position: Position) {
        if (gameState.promotionSelection.isNotEmpty()) return
        val activePosition = gameState.activePosition
        val piece = gameState.piecesByPosition[position]
        val move = gameState.legalMoves.find { it.from == gameState.activePosition && it.to == position }

        gameState =
            if (move is Promotion)
                gameState.copy(
                    activePosition = null,
                    legalMoves = emptyList(),
                    promotionSelection = gameState.legalMoves.filterIsInstance<Promotion>().filter { it.to == move.to },
                )
            else if (move != null)
                gameState.applyMove(move)
            else if (activePosition != null) gameState.copy(
                activePosition = null,
                legalMoves = emptyList(),
            )
            else if (piece != null)
                gameState.copy(
                    activePosition = position,
                    legalMoves = legalMoves(piece = piece, gameState = gameState),
                )
            else gameState

        update()
    }

    override fun onDragStart(position: Position) {
        if (gameState.promotionSelection.isNotEmpty()) return

        val squareSize = uiState.squareSize
        val piece = gameState.piecesByPosition[position] ?: error("Can only drag when piece is on square")
        gameState = gameState.copy(
            activePosition = position,
            legalMoves = engine.getLegalMoves(gameState, position)
        )
        uiState = uiState.copy(
            pieceMinDragOffset = Offset(
                (-position.file.ordinal * squareSize - squareSize / 2).toFloat(),
                (-(7 - position.rank.ordinal) * squareSize - squareSize / 2).toFloat(),
            ),
            pieceMaxDragOffset = Offset(
                ((7 - position.file.ordinal) * squareSize + squareSize / 2).toFloat(),
                (position.rank.ordinal * squareSize + squareSize / 2).toFloat(),
            ),
        )
        update()
    }

    override fun onDrag(offset: Offset) {
        if (gameState.promotionSelection.isNotEmpty()) return

        val newOffset = uiState.pieceDragOffset + offset
        val min = uiState.pieceMinDragOffset
        val max = uiState.pieceMaxDragOffset
            uiState = uiState.copy(
                pieceDragOffset = newOffset,
                constrainedPieceDragOffset = Offset(
                    min(max(min.x, newOffset.x), max.x),
                    min(max(min.y, newOffset.y), max.y),
                )
        )
        update()
    }

    override fun onDragEnd() {
        if (gameState.promotionSelection.isNotEmpty()) return

        val fromPosition = gameState.activePosition ?: error("Can only drag with active square")
        val toPosition = fromPosition.getTargetPosition(
            offset = uiState.constrainedPieceDragOffset,
            squareSize = uiState.squareSize,
        )

        val move = gameState.legalMoves.find { it.from == gameState.activePosition && it.to == toPosition }

        if (move is Promotion) {
            gameState = gameState.copy(
                promotionSelection = gameState.legalMoves.filterIsInstance<Promotion>().filter { it.to == move.to },
            )
        } else if (move != null) {
            engine.makeMove(gameState, move)
        } else {
            uiState = uiState.copy(
                pieceDragOffset = Offset.Zero,
                pieceMinDragOffset = Offset.Zero,
                pieceMaxDragOffset = Offset.Zero,
                constrainedPieceDragOffset = Offset.Zero,
            )
        }

        update()
    }

    override fun applyPromotion(promotion: Promotion) {
        engine.makeMove(gameState, promotion)
        update()
    }

    override fun cancelPromotion() {
        gameState = gameState.copy(
            promotionSelection = emptyList(),
        )

        uiState = uiState.copy(
            pieceDragOffset = Offset.Zero,
            pieceMinDragOffset = Offset.Zero,
            pieceMaxDragOffset = Offset.Zero,
            constrainedPieceDragOffset = Offset.Zero,
        )

        update()
    }


    override fun onSquareSizeChanged(squareSize: Int) {
            uiState = uiState.copy(squareSize = squareSize)
        // No update required
    }

    override fun updateOnStateChanges(onState: GameStateObserver) {
        observers += onState
    }

    private fun update() {
        observers.forEach { it(gameState) }
    }
}

fun Position.getTargetPosition(offset: Offset, squareSize: Int): Position? {
    fun Float.addDirectionalHalf() = if (this > 0) this + 0.499 else this - 0.499

    val addFiles = (offset.x / squareSize).addDirectionalHalf().toInt()
    val addRanks = -(offset.y / squareSize).addDirectionalHalf().toInt()
    val newFileOrdinal = file.ordinal + addFiles
    val newRankOrdinal = rank.ordinal + addRanks

    if (newFileOrdinal < 0 || newFileOrdinal > 7 || newRankOrdinal < 0 || newRankOrdinal > 7) {
        return null
    }

    val newFile = File.entries[newFileOrdinal]
    val newRank = Rank.entries[newRankOrdinal]
    return Position.fromFileAndRank(newFile, newRank)
}

fun GameState.applyMove(move: Move): GameState {
    if (move.from == move.to) return copy(
        activePosition = null,
        legalMoves = emptyList(),
    )

    return copy(
        activePosition = null,
        legalMoves = emptyList(),
        boardSnapshots = boardSnapshots.dropLast(1) + BoardSnapshot(
            piecesByPosition = piecesByPosition,
            sideToPlay = sideToPlay,
            move = move,
        ) + BoardSnapshot(
            piecesByPosition = move.applyOn(piecesByPosition),
            sideToPlay = sideToPlay.opposite,
            move = null,
        ),
        promotionSelection = emptyList(),
    )
}
fun GameState.simulateMove(move: Move): GameState {
    return copy(
        boardSnapshots = listOf(
            BoardSnapshot(
                piecesByPosition = move.applyOn(piecesByPosition),
                sideToPlay = sideToPlay.opposite
            )
        )
    )
}


private fun legalMoves(piece: Piece, gameState: GameState): List<Move> {
    if (piece.side != gameState.sideToPlay) return emptyList()

    val pseudoLegalMoves = piece.pseudoLegalMoves(gameState, true)

    return pseudoLegalMoves.filter { !gameState.isKingInCheck(it) }
}

private fun GameState.isKingInCheck(move: Move): Boolean {
    val nextGameState = simulateMove(move)

    val opponentSide = move.piece.side.opposite
    val opponentPieces = nextGameState.piecesByPosition.values.filter { it.side == opponentSide }

    return opponentPieces
        .flatMap { it.pseudoLegalMoves(nextGameState) }
        .filterIsInstance<CapturingMove>()
        .any { it.capturedPiece is King }
}


