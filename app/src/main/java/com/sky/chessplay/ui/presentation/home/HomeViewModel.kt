package com.sky.chessplay.ui.presentation.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sky.chessplay.data.remote.dto.response.AiModelInfo
import com.sky.chessplay.domain.model.chess.Side
import com.sky.chessplay.domain.repository.AiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val aiRepository: AiRepository
) : ViewModel() {

    // =========================
    // UI STATE
    // =========================

    var showAiModal by mutableStateOf(false)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set


    // =========================
    // AI CONFIG
    // =========================

    var aiModels by mutableStateOf<List<AiModelInfo>>(emptyList())
        private set

    var selectedModel by mutableStateOf("best_model")
        private set

    var difficulty by mutableIntStateOf(3)
        private set

    var playerColor by mutableStateOf(Side.WHITE)
        private set


    // =========================
    // INIT
    // =========================

    init {
        loadModels()
    }


    // =========================
    // LOAD AI MODELS
    // =========================

    private fun loadModels() {

        viewModelScope.launch {

            isLoading = true

            try {

                val response = aiRepository.getAvailableModels()

                aiModels = response.models ?: emptyList()

                selectedModel =
                    response.default
                        ?: response.models?.firstOrNull()?.key
                                ?: "best_model"

            } catch (e: Exception) {

                aiModels = listOf(
                    AiModelInfo(
                        key = "best_model",
                        display = "Default AI"
                    )
                )

                selectedModel = "best_model"

                error = e.message
            } finally {

                isLoading = false
            }
        }
    }


    // =========================
    // MODAL
    // =========================

    fun openAiModal() {
        showAiModal = true
    }

    fun closeAiModal() {
        showAiModal = false
    }


    // =========================
    // SETTINGS
    // =========================

    fun selectModel(model: String) {
        selectedModel = model
    }

    fun changeDifficulty(value: Int) {
        difficulty = value.coerceIn(1, 10)
    }

    fun changePlayerColor(side: Side) {
        playerColor = side
    }


    // =========================
    // ERROR
    // =========================

    fun clearError() {
        error = null
    }


    // =========================
    // BUILD CONFIG
    // =========================

    fun buildAiConfig(): AiGameConfig {

        return AiGameConfig(
            model = selectedModel,
            difficulty = difficulty,
            side = playerColor
        )
    }
}
data class AiGameConfig(
    val model: String,
    val difficulty: Int,
    val side: Side
)