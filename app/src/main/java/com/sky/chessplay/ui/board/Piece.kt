package view.board

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.sky.chessplay.domain.model.Position
import com.sky.chessplay.ui.graphics.SvgCache
import model.board.Square

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Piece(
    square: Square,
    onDragStart: (Position) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
) {
    val piece = square.piece ?: return
    val dragOffset = if (square.isActive) square.gameState.uiState.constrainedPieceDragOffset else Offset.Zero

    val context = LocalContext.current
    val drawable = remember(piece.asset) {
        SvgCache.get(context, piece.asset)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { onDragStart(square.position) },
                    onDrag = { _, dragAmount -> onDrag(dragAmount) },
                    onDragEnd = { onDragEnd() }
                )
            }
            .graphicsLayer {
                translationX = dragOffset.x
                translationY = dragOffset.y
                 scaleX = 2.5f
                 scaleY = 2.5f
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = rememberDrawablePainter(drawable),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize(0.35f)
        )
    }
}
