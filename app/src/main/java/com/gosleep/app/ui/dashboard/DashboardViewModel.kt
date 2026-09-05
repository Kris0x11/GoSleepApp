package com.gosleep.app.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gosleep.app.data.repository.SleepRepository
import com.gosleep.app.domain.EnergyLevelCalculator
import com.gosleep.app.domain.PlantGrowthCalculator
import com.gosleep.app.domain.StreakCalculator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalTime
import com.gosleep.app.data.repository.SleepQuality
data class DashboardUiState(
    val greeting: String = "Good Evening",
    val bedtimeMinutes: Int = 0,
    val hasLastNightScore: Boolean = false,
    val lastSleepScore: Int = 0,
    val streakDays: Int = 0,
    val streakLabel: String = "",
    val energyPercent: Int = 0,
    val energyLabel: String = "",
    val energyEmoji: String = "",
    val plantGrowthPercent: Int = 0,
    val plantStageLabel: String = "",
)

/**
 * Cuore operativo dell'app (Site-Map: DASHBOARD). Aggrega bedtime, sleep score,
 * streak, energy e crescita della pianta in un'unica sorgente di verità reattiva.
 */
class DashboardViewModel(
    private val sleepRepository: SleepRepository,
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = combine(
        sleepRepository.bedtimeMinutes,
        sleepRepository.streakDays,
        sleepRepository.plantGrowthPercent,
        sleepRepository.lastSleepScore,
    ) { bedtime, streak, plant, score ->
        val energyPercent = EnergyLevelCalculator.percent(score)
        val plantStage = PlantGrowthCalculator.stage(plant)
        DashboardUiState(
            greeting = greetingForNow(),
            bedtimeMinutes = bedtime,
            hasLastNightScore = score > 0,
            lastSleepScore = score,
            streakDays = streak,
            streakLabel = StreakCalculator.streakLabel(streak),
            energyPercent = energyPercent,
            energyLabel = EnergyLevelCalculator.label(energyPercent),
            energyEmoji = EnergyLevelCalculator.emoji(energyPercent),
            plantGrowthPercent = plant,
            plantStageLabel = PlantGrowthCalculator.stageLabel(plantStage),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardUiState())

    fun onBedtimeChanged(minutes: Int) {
        viewModelScope.launch { sleepRepository.setBedtime(minutes) }
    }

    private fun greetingForNow(): String = when (LocalTime.now().hour) {
        in 5..11 -> "Hello!"
        in 12..17 -> "Good Afternoon"
        in 18..21 -> "Good Evening"
        else -> "Good Night"
    }
    fun simulateWeek(quality: SleepQuality) {
        viewModelScope.launch { sleepRepository.simulateWeek(quality) }
    }

    fun resetDebugData() {
        viewModelScope.launch { sleepRepository.resetDebugData() }
    }
}
