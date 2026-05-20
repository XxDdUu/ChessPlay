import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.sky.chessplay.ui.presentation.chess.ai_play.AiPlayViewModel

@Composable
fun AiPlayRoute(
    viewModel: AiPlayViewModel = hiltViewModel()
) {
    AiPlayScreen(
        gameState = viewModel.gameState,
        uiState = viewModel.uiState,
        isThinking = viewModel.isThinking,
        isLoading = viewModel.isLoading,
        apiError = viewModel.apiError,
        aiModels = viewModel.aiModels,
        selectedModel = viewModel.selectedModel,
        difficulty = viewModel.difficulty,
        playerColor = viewModel.playerColor,
        gameId = viewModel.gameId,
        statusText = viewModel.getStatusText(),
        history = viewModel.gameHistory,

        onSelectModel = {
            viewModel.selectedModel = it
        },

        onDifficultyChange = {
            viewModel.difficulty = it
        },

        onPlayerColorChange = {
            viewModel.playerColor = it
        },

        onStartGame = viewModel::startGame,
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