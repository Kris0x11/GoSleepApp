package com.gosleep.app.ui.prep

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gosleep.app.data.repository.SleepRepository
import com.gosleep.app.domain.BedtimeCountdownCalculator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SleepPreparationUiState(
    val bedtimeMinutes: Int = 0,
    val countdownLabel: String = "",
    val progressFraction: Float = 0f,
)

/**
 * Schermata "Sleep Preparation" (img countdown "4h 56m until bedtime"):
 * mostra il tempo restante al Reverse Alarm e permette di modificarne l'orario.
 */
class SleepPreparationViewModel(
    private val sleepRepository: SleepRepository,
) : ViewModel() {

    val uiState: StateFlow<SleepPreparationUiState> = sleepRepository.bedtimeMinutes
        .map { minutes ->
            SleepPreparationUiState(
                bedtimeMinutes = minutes,
                countdownLabel = BedtimeCountdownCalculator.countdown(minutes).label(),
                progressFraction = BedtimeCountdownCalculator.progressFraction(minutes),
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SleepPreparationUiState())

    fun onBedtimeChanged(minutes: Int) {
        viewModelScope.launch { sleepRepository.setBedtime(minutes) }
    }
}
