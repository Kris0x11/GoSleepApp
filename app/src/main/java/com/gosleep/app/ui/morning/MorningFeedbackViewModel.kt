package com.gosleep.app.ui.morning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gosleep.app.data.repository.SleepRepository
import com.gosleep.app.domain.SleepScoreCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class MorningFeedbackUiState(
    val isLoading: Boolean = true,
    val sleepScore: Int = 0,
    val motivationalLabel: String = "",
    val streakDays: Int = 0,
    val plantGrowthPercent: Int = 0,
    val plantJustGrew: Boolean = false,
)

/**
 * Modulo "Morning Feedback" (img finale, "Good Morning!" / "Your Plant Grew! +10% Growth"):
 * chiude il ciclo dell'azione mostrando lo Sleep Score, la streak e la crescita della pianta.
 */
class MorningFeedbackViewModel(
    private val sleepRepository: SleepRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MorningFeedbackUiState())
    val uiState: StateFlow<MorningFeedbackUiState> = _uiState.asStateFlow()

    fun onWakeUp(usedRelaxOrBrainDump: Boolean) {
        viewModelScope.launch {
            val score = sleepRepository.completeMorningFeedback(usedRelaxOrBrainDump)
            // I nuovi valori di streak/plant sono già persistiti da completeMorningFeedback;
            // li leggiamo di nuovo per popolare la UI.
            val streak = sleepRepository.streakDays.first()
            val plantGrowth = sleepRepository.plantGrowthPercent.first()

            _uiState.value = MorningFeedbackUiState(
                isLoading = false,
                sleepScore = score,
                motivationalLabel = SleepScoreCalculator.motivationalLabel(score),
                streakDays = streak,
                plantGrowthPercent = plantGrowth,
                plantJustGrew = streak > 0,
            )
        }
    }
}
