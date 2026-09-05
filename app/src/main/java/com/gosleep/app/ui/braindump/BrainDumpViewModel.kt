package com.gosleep.app.ui.braindump

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gosleep.app.data.local.entity.BrainDumpEntity
import com.gosleep.app.data.repository.BrainDumpRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class BrainDumpUiState(
    val draftText: String = "",
    val isRecordingVoice: Boolean = false,
)

/**
 * Modulo "Brain Dump" (img .1C): scarico del carico cognitivo prima di dormire.
 * Minimalismo funzionale: scelta binaria immediata tra scrivere o parlare.
 */
class BrainDumpViewModel(
    private val repository: BrainDumpRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(BrainDumpUiState())
    val uiState: StateFlow<BrainDumpUiState> = _uiState

    val notes: StateFlow<List<BrainDumpEntity>> = repository.observeUnresolvedNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onDraftChanged(text: String) {
        _uiState.value = _uiState.value.copy(draftText = text)
    }

    fun onSaveTextNote() {
        val text = _uiState.value.draftText
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.addTextNote(text)
            _uiState.value = _uiState.value.copy(draftText = "")
        }
    }

    fun onStartVoiceRecording() {
        _uiState.value = _uiState.value.copy(isRecordingVoice = true)
    }

    /**
     * Chiamata dal livello UI/piattaforma quando la trascrizione vocale è pronta
     * (in produzione collegata a SpeechRecognizer di Android).
     */
    fun onVoiceTranscriptReady(transcript: String) {
        viewModelScope.launch {
            if (transcript.isNotBlank()) repository.addVoiceNote(transcript)
            _uiState.value = _uiState.value.copy(isRecordingVoice = false)
        }
    }

    fun onDeleteNote(note: BrainDumpEntity) {
        viewModelScope.launch { repository.delete(note) }
    }
}
