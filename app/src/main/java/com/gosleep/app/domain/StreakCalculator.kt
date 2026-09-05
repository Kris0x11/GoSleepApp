package com.gosleep.app.domain

/**
 * Regole della "streak" (giorni consecutivi di successo), mostrata in dashboard.
 * Estratta come oggetto puro per essere testata senza dipendenze Android/DataStore.
 */
object StreakCalculator {

    /**
     * @param currentStreak streak attuale salvata.
     * @param nightSucceeded true se l'orario è stato rispettato
     */
    fun nextStreak(currentStreak: Int, nightSucceeded: Boolean): Int =
        if (nightSucceeded) currentStreak + 1 else 0

    /** Testo del badge streak */
    fun streakLabel(streak: Int): String = when (streak) {
        0 -> "Start tonight"
        1 -> "1 day in a row"
        else -> "$streak days in a row"
    }
}
