package com.gosleep.app.ui.relax

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class BreathPhase { INHALE, EXHALE }

data class RelaxModeUiState(
    val breathPhase: BreathPhase = BreathPhase.INHALE,
    val whiteNoiseOn: Boolean = false,
)

/**
 * "Relax Mode": Low-interaction UI, cerchio pulsante isomorfico che guida la
 * respirazione (Inhale/Exhale), come descritto per il caso d'uso Roberto.
 */
class RelaxModeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RelaxModeUiState())
    val uiState: StateFlow<RelaxModeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            while (true) {
                delay(INHALE_MILLIS)
                _uiState.value = _uiState.value.copy(breathPhase = BreathPhase.EXHALE)
                delay(EXHALE_MILLIS)
                _uiState.value = _uiState.value.copy(breathPhase = BreathPhase.INHALE)
            }
        }
    }

    fun onToggleWhiteNoise() {
        _uiState.value = _uiState.value.copy(whiteNoiseOn = !_uiState.value.whiteNoiseOn)
    }

    companion object {
        const val INHALE_MILLIS = 4_000L
        const val EXHALE_MILLIS = 4_000L
    }
}
