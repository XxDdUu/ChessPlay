package view.board

import androidx.compose.animation.core.animateOffsetAsState
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
import com.sky.chessplay.ui.state.UiState
import model.board.Square

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Piece(
    square: Square,
    uiState: UiState,
    enableDrag: Boolean,
    disableAnimation: Boolean,
    onDragStart: (Position) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
) {
    val piece = square.piece ?: return

    val isDragging = square.isActive

    val targetOffset = if (isDragging) {
        uiState.constrainedPieceDragOffset
    } else Offset.Zero

    val offset = if (disableAnimation) {
        targetOffset
    } else {
        animateOffsetAsState(targetOffset).value
    }

    val context = LocalContext.current
    val drawable = remember(piece.asset) {
        SvgCache.get(context, piece.asset)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()

            .then(
                if (enableDrag) {
                    Modifier.pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { onDragStart(square.position) },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                onDrag(dragAmount)
                            },
                            onDragEnd = onDragEnd
                        )
                    }
                } else Modifier
            )

            .graphicsLayer {
                translationX = offset.x
                translationY = offset.y

                if (isDragging) {
                    scaleX = 1.2f
                    scaleY = 1.2f
                } else {
                    scaleX = 2.5f
                    scaleY = 2.5f
                }
            },

        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = rememberDrawablePainter(drawable),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize(0.35f)
        )
    }
}
