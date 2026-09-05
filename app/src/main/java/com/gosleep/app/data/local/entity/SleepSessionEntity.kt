package com.gosleep.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Rappresenta una notte monitorata da GoSleep.
 * Popolata alla chiusura ("I'm going to sleep now") e completata al risveglio (Morning Feedback).
 */
@Entity(tableName = "sleep_sessions")
data class SleepSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dateEpochDay: Long,          // giorno (LocalDate.toEpochDay) a cui la sessione si riferisce
    val bedtimeTargetMinutes: Int,   // orario target impostato dall'utente (minuti da mezzanotte)
    val actualSleepStartMinutes: Int?, // quando l'utente ha effettivamente premuto "sleep now" / completato la routine
    val wakeUpEpochMillis: Long?,    // timestamp dello sblocco al risveglio
    val distractionResisted: Boolean, // true se ha premuto "Continue to Sleep" invece di "stay awake"
    val routineCompleted: Boolean,
    val sleepScore: Int?,            // calcolato al risveglio (0-100)
)
