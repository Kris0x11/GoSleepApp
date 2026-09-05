package com.gosleep.app.ui.routine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gosleep.app.data.repository.SleepRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RoutineStep(
    val id: String,
    val emoji: String,
    val title: String,
    val subtitle: String,
)

/**
 * I 6 step della sleep routine
 * micro-tasking sequenziale.
 */
val ROUTINE_STEPS = listOf(
    RoutineStep("charge_phone", "🔌", "Put your phone on the charger", "Away from your bed"),
    RoutineStep("screens_off", "📺", "Turn off all screens", "No more stimulation"),
    RoutineStep("dim_lights", "💡", "Dim the lights", "Signal your brain it's night"),
    RoutineStep("bathroom", "🚿", "Go to the bathroom", "Complete your routine"),
    RoutineStep("get_into_bed", "🛏️", "Get into bed", "Lie down comfortably"),
    RoutineStep("close_eyes", "😌", "Close your eyes", "You did it!"),
)

data class RoutineFlowUiState(
    val currentStepIndex: Int = 0,
    val completed: Boolean = false,
) {
    val currentStep: RoutineStep get() = ROUTINE_STEPS[currentStepIndex.coerceIn(0, ROUTINE_STEPS.lastIndex)]
    val stepNumber: Int get() = currentStepIndex + 1
    val totalSteps: Int get() = ROUTINE_STEPS.size
}

class RoutineFlowViewModel(
    private val sleepRepository: SleepRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoutineFlowUiState())
    val uiState: StateFlow<RoutineFlowUiState> = _uiState.asStateFlow()

    /** "Done, Next Step" — riduzione del golfo dell'esecuzione: un solo tap per step. */
    fun onStepDone() {
        val state = _uiState.value
        if (state.completed) return

        if (state.currentStepIndex >= ROUTINE_STEPS.lastIndex) {
            _uiState.value = state.copy(completed = true)
            viewModelScope.launch { sleepRepository.recordRoutineCompleted() }
        } else {
            _uiState.value = state.copy(currentStepIndex = state.currentStepIndex + 1)
        }
    }

    fun onSkipRoutine() {
        _uiState.value = _uiState.value.copy(completed = true)
    }
}
