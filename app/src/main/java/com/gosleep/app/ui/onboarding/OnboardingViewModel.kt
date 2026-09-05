package com.gosleep.app.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gosleep.app.data.datastore.UserPreferencesRepository
import kotlinx.coroutines.launch

data class OnboardingQuestion(val question: String, val options: List<String>)

/** Le 4 domande esatte del questionario di onboarding. */
val ONBOARDING_QUESTIONS = listOf(
    OnboardingQuestion(
        "When it's time for bed, I usually...",
        listOf(
            "Keep scrolling \"just one more minute\"",
            "Feel too tired to move",
            "Start worrying about tomorrow",
            "Go to bed without issues",
        ),
    ),
    OnboardingQuestion(
        "My biggest bedtime struggle is...",
        listOf(
            "I can't stop what I'm doing",
            "Getting up requires too much effort",
            "Racing thoughts keep me up",
            "I don't have bedtime struggles",
        ),
    ),
    OnboardingQuestion(
        "How do you feel about going to bed?",
        listOf(
            "It feels like missing out on fun",
            "I'm exhausted but can't start the routine",
            "Anxious about not falling asleep",
            "I look forward to it",
        ),
    ),
    OnboardingQuestion(
        "When trying to sleep, my mind...",
        listOf(
            "Craves stimulation and entertainment",
            "Is foggy but my body won't move",
            "Races with worries and to-dos",
            "Relaxes easily",
        ),
    ),
)

/**
 * Gestisce la fase "ONBOARDING: L'Ingresso" del documento:
 * benvenuto -> questionario (4 domande) -> dashboard con bedtime di default (22:30).
 */
class OnboardingViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    fun completeOnboarding(answers: List<String>, onDone: () -> Unit) {
        viewModelScope.launch {
            userPreferencesRepository.setPersonaHint(answers.joinToString("|"))
            userPreferencesRepository.setOnboardingComplete(true)
            onDone()
        }
    }
}
