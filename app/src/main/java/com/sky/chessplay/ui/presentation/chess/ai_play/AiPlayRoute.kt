import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sky.chessplay.ui.presentation.chess.ai_play.AiPlayScreen
import com.sky.chessplay.ui.presentation.chess.ai_play.AiPlayViewModel

@Composable
fun AiPlayRoute(
    navController: NavHostController,
    viewModel: AiPlayViewModel = hiltViewModel()
) {

    AiPlayScreen(
        gameState = viewModel.gameState,
        uiState = viewModel.uiState,

        navController = navController,

        isThinking = viewModel.isThinking,
        isLoading = viewModel.isLoading,
        apiError = viewModel.apiError,

        gameId = viewModel.gameId,

        statusText = viewModel.getStatusText(),
        history = viewModel.gameHistory,

        onResignGame = viewModel::resignGame,
        onDismissError = viewModel::resetError,

        onClick = viewModel::onClick,
        onDragStart = viewModel::onDragStart,
        onDrag = viewModel::onDrag,
        onDragEnd = viewModel::onDragEnd,

        onApplyPromotion = viewModel::applyPromotion,
        onCancelPromotion = viewModel::cancelPromotion,

        onSquareSizeChanged = viewModel::onSquareSizeChanged
    )
}