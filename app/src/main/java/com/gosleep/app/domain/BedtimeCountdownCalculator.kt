package com.gosleep.app.domain

import java.time.Duration
import java.time.LocalTime

/**
 * Calcola il tempo restante fino al Reverse Alarm
 *
 */
object BedtimeCountdownCalculator {

    data class Countdown(val hours: Int, val minutes: Int) {
        fun label(): String = when {
            hours > 0 -> "${hours}h ${minutes}m"
            else -> "${minutes}m"
        }
    }

    fun countdown(bedtimeMinutes: Int, now: LocalTime = LocalTime.now()): Countdown {
        val nowMinutes = now.hour * 60 + now.minute
        var diff = bedtimeMinutes - nowMinutes
        if (diff <= 0) diff += 24 * 60
        return Countdown(hours = diff / 60, minutes = diff % 60)
    }

    /** Frazione (0f-1f) di quanto tempo è trascorso in una finestra di 8 ore prima del bedtime, per l'anello. */
    fun progressFraction(bedtimeMinutes: Int, now: LocalTime = LocalTime.now(), windowHours: Int = 8): Float {
        val nowMinutes = now.hour * 60 + now.minute
        var diff = bedtimeMinutes - nowMinutes
        if (diff <= 0) diff += 24 * 60
        val windowMinutes = windowHours * 60
        val elapsed = (windowMinutes - diff).coerceIn(0, windowMinutes)
        return elapsed / windowMinutes.toFloat()
    }
}
