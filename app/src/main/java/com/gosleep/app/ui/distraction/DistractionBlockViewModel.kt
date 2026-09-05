package com.gosleep.app.ui.distraction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gosleep.app.data.repository.SleepRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Stato della schermata di blocco distrazioni (caso d'uso Marco, img .1A/.1A-2).
 * Il tasto "I choose to stay awake" resta nascosto/disabilitato per [FORCED_FRICTION_SECONDS]
 * secondi: il "forced friction" descritto nel documento, pensato per disinnescare l'impulso.
 */
data class DistractionBlockUiState(
    val secondsRemaining: Int = FORCED_FRICTION_SECONDS,
    val stayAwakeButtonVisible: Boolean = false,
    val resolution: Resolution? = null,
) {
    enum class Resolution { SLEEP_NOW, STAYED_AWAKE }

    companion object {
        const val FORCED_FRICTION_SECONDS = 5
    }
}

class DistractionBlockViewModel(
    private val sleepRepository: SleepRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DistractionBlockUiState())
    val uiState: StateFlow<DistractionBlockUiState> = _uiState.asStateFlow()

    private var countdownJob: Job? = null

    init {
        startCountdown()
    }

    private fun startCountdown() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            for (remaining in DistractionBlockUiState.FORCED_FRICTION_SECONDS downTo 1) {
                _uiState.value = _uiState.value.copy(secondsRemaining = remaining, stayAwakeButtonVisible = false)
                delay(1_000)
            }
            _uiState.value = _uiState.value.copy(secondsRemaining = 0, stayAwakeButtonVisible = true)
        }
    }

    /** L'utente ha premuto il tasto viola "Continue to Sleep": riduzione del golfo dell'esecuzione. */
    fun onContinueToSleep(bedtimeTargetMinutes: Int, onSleepModeActivated: () -> Unit) {
        countdownJob?.cancel()
        viewModelScope.launch {
            sleepRepository.recordDistractionResisted(bedtimeTargetMinutes)
            _uiState.value = _uiState.value.copy(resolution = DistractionBlockUiState.Resolution.SLEEP_NOW)
            onSleepModeActivated()
        }
    }

    /** Disponibile solo dopo i 5 secondi di forced friction. */
    fun onChooseToStayAwake() {
        if (!_uiState.value.stayAwakeButtonVisible) return
        countdownJob?.cancel()
        _uiState.value = _uiState.value.copy(resolution = DistractionBlockUiState.Resolution.STAYED_AWAKE)
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}
