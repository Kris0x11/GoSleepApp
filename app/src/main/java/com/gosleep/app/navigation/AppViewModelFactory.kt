package com.gosleep.app.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.gosleep.app.GoSleepApplication
import com.gosleep.app.ui.braindump.BrainDumpViewModel
import com.gosleep.app.ui.dashboard.DashboardViewModel
import com.gosleep.app.ui.distraction.DistractionBlockViewModel
import com.gosleep.app.ui.morning.MorningFeedbackViewModel
import com.gosleep.app.ui.onboarding.OnboardingViewModel
import com.gosleep.app.ui.prep.SleepPreparationViewModel
import com.gosleep.app.ui.relax.RelaxModeViewModel
import com.gosleep.app.ui.routine.RoutineFlowViewModel

/**
 * Factory manuale unica per tutti i ViewModel: recupera i repository dalla
 * [GoSleepApplication] così ogni schermata riceve dipendenze.
 */
class AppViewModelFactory(private val app: GoSleepApplication) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return when (modelClass) {
            OnboardingViewModel::class.java ->
                OnboardingViewModel(app.userPreferencesRepository) as T

            DashboardViewModel::class.java ->
                DashboardViewModel(app.sleepRepository) as T

            DistractionBlockViewModel::class.java ->
                DistractionBlockViewModel(app.sleepRepository) as T

            RoutineFlowViewModel::class.java ->
                RoutineFlowViewModel(app.sleepRepository) as T

            BrainDumpViewModel::class.java ->
                BrainDumpViewModel(app.brainDumpRepository) as T

            RelaxModeViewModel::class.java ->
                RelaxModeViewModel() as T

            SleepPreparationViewModel::class.java ->
                SleepPreparationViewModel(app.sleepRepository) as T

            MorningFeedbackViewModel::class.java ->
                MorningFeedbackViewModel(app.sleepRepository) as T

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
