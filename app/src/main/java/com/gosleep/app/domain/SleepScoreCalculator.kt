package com.gosleep.app.domain

/**
 * Calcola lo Sleep Score (0-100) mostrato nel Morning Feedback ("Sleep Score X out of 100").
 * Logica volutamente semplice e trasparente, in linea con il posizionamento "meno analitico,
 * più orientato al supporto" descritto nella documentazione (vs. competitor come Sleep Cycle).
 */
object SleepScoreCalculator {

    /**
     * @param wentToBedOnTime true se l'utente ha iniziato il sonno entro la tolleranza
     *        dall'orario target (bedtimeTargetMinutes).
     * @param distractionResisted true se ha premuto "Continue to Sleep" invece di "stay awake".
     * @param routineCompleted true se ha completato tutti gli step della sleep routine.
     * @param usedRelaxOrBrainDump true se ha usato Relax Mode o Brain Dump (gestione attiva dello stress).
     */
    fun calculate(
        wentToBedOnTime: Boolean,
        distractionResisted: Boolean,
        routineCompleted: Boolean,
        usedRelaxOrBrainDump: Boolean,
    ): Int {
        var score = 40 // base: essersi almeno svegliati e aperto l'app

        if (distractionResisted) score += 25
        if (routineCompleted) score += 20
        if (wentToBedOnTime) score += 10
        if (usedRelaxOrBrainDump) score += 5

        return score.coerceIn(0, 100)
    }

    /**
     * Etichetta motivazionale coerente con il pattern "Loss Aversion / Negativity" +
     * "Goal-Gradient Effect" descritti nel documento (es. "Keep Trying").
     */
    fun motivationalLabel(score: Int): String = when {
        score >= 85 -> "Great Night!"
        score >= 60 -> "Good Progress"
        else -> "Keep Trying"
    }
}
