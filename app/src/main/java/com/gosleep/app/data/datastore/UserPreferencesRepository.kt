package com.gosleep.app.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "gosleep_prefs")

/**
 * Preferenze utente  e stato di gamification, coerenti con la sezione
 *
 */
class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
        val BEDTIME_MINUTES = intPreferencesKey("bedtime_minutes") // minuti da mezzanotte, es. 22:30 -> 1350
        val STREAK_DAYS = intPreferencesKey("streak_days")
        val PLANT_GROWTH_PERCENT = intPreferencesKey("plant_growth_percent")
        val LAST_SLEEP_SCORE = intPreferencesKey("last_sleep_score")
        val PERSONA_HINT = stringPreferencesKey("persona_hint") // risposta onboarding, usata per personalizzare i copy
    }

    val onboardingComplete: Flow<Boolean> =
        context.dataStore.data.map { it[Keys.ONBOARDING_COMPLETE] ?: false }

    val bedtimeMinutes: Flow<Int> =
        context.dataStore.data.map { it[Keys.BEDTIME_MINUTES] ?: DEFAULT_BEDTIME_MINUTES }

    val streakDays: Flow<Int> =
        context.dataStore.data.map { it[Keys.STREAK_DAYS] ?: 0 }

    val plantGrowthPercent: Flow<Int> =
        context.dataStore.data.map { it[Keys.PLANT_GROWTH_PERCENT] ?: 0 }

    val lastSleepScore: Flow<Int> =
        context.dataStore.data.map { it[Keys.LAST_SLEEP_SCORE] ?: 0 }

    suspend fun setOnboardingComplete(complete: Boolean) {
        context.dataStore.edit { it[Keys.ONBOARDING_COMPLETE] = complete }
    }

    suspend fun setBedtimeMinutes(minutes: Int) {
        context.dataStore.edit { it[Keys.BEDTIME_MINUTES] = minutes }
    }

    suspend fun setPersonaHint(hint: String) {
        context.dataStore.edit { it[Keys.PERSONA_HINT] = hint }
    }

    /** Chiamata quando una notte viene completata con successo (routine o distraction block superati). */
    suspend fun recordSuccessfulNight(newSleepScore: Int) {
        context.dataStore.edit { prefs ->
            val currentStreak = prefs[Keys.STREAK_DAYS] ?: 0
            val currentGrowth = prefs[Keys.PLANT_GROWTH_PERCENT] ?: 0
            prefs[Keys.STREAK_DAYS] = currentStreak + 1
            prefs[Keys.PLANT_GROWTH_PERCENT] = (currentGrowth + PLANT_GROWTH_STEP).coerceAtMost(100)
            prefs[Keys.LAST_SLEEP_SCORE] = newSleepScore
        }
    }

    /** Chiamata quando l'utente sceglie "I choose to stay awake": la streak si azzera. */
    suspend fun recordBrokenStreak(newSleepScore: Int) {
        context.dataStore.edit { prefs ->
            prefs[Keys.STREAK_DAYS] = 0
            prefs[Keys.LAST_SLEEP_SCORE] = newSleepScore
        }
    }

    companion object {
        const val DEFAULT_BEDTIME_MINUTES = 22 * 60 + 30 // 22:30, come nel mock "It's Time to Sleep"
        const val PLANT_GROWTH_STEP = 10 // "+10% Growth" come da schermata Morning Feedback
    }

    /** DEBUG: azzera streak, crescita pianta e ultimo punteggio, per ripartire da zero tra un test e l'altro. */
    suspend fun resetDebugData() {
        context.dataStore.edit { prefs ->
            prefs[Keys.STREAK_DAYS] = 0
            prefs[Keys.PLANT_GROWTH_PERCENT] = 0
            prefs[Keys.LAST_SLEEP_SCORE] = 0
        }
    }
}
