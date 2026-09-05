package com.gosleep.app.domain

/**
 * Traduce l'ultimo Sleep Score in un "Energy Level" percepito, mostrato in dashboard
 * (card "Energy Level" con emoji + etichetta + percentuale).
 */
object EnergyLevelCalculator {

    fun percent(lastSleepScore: Int): Int = lastSleepScore.coerceIn(0, 100)

    fun label(percent: Int): String = when {
        percent >= 80 -> "Energized"
        percent >= 60 -> "Good"
        percent >= 35 -> "Tired"
        else -> "Exhausted"
    }

    fun emoji(percent: Int): String = when {
        percent >= 80 -> "⚡"
        percent >= 60 -> "🙂"
        percent >= 35 -> "😴"
        else -> "😩"
    }
}
