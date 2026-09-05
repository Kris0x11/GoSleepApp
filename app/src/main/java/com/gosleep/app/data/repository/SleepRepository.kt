package com.gosleep.app.data.repository

import com.gosleep.app.data.datastore.UserPreferencesRepository
import com.gosleep.app.data.local.dao.SleepSessionDao
import com.gosleep.app.data.local.entity.SleepSessionEntity
import com.gosleep.app.domain.PlantGrowthCalculator
import com.gosleep.app.domain.SleepScoreCalculator
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import com.gosleep.app.notification.ReverseAlarmScheduler

enum class SleepQuality { POOR, NORMAL, GOOD }
class SleepRepository(
    private val sleepSessionDao: SleepSessionDao,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val reverseAlarmScheduler: ReverseAlarmScheduler
    ) {

    val bedtimeMinutes: Flow<Int> = userPreferencesRepository.bedtimeMinutes
    val streakDays: Flow<Int> = userPreferencesRepository.streakDays
    val plantGrowthPercent: Flow<Int> = userPreferencesRepository.plantGrowthPercent
    val lastSleepScore: Flow<Int> = userPreferencesRepository.lastSleepScore

    fun observeLatestSession(): Flow<SleepSessionEntity?> = sleepSessionDao.observeLatest()

    fun observeRecentSessions(limit: Int = 30): Flow<List<SleepSessionEntity>> =
        sleepSessionDao.observeRecent(limit)

    suspend fun setBedtime(minutes: Int) {
        userPreferencesRepository.setBedtimeMinutes(minutes)
        reverseAlarmScheduler.schedule(minutes) // <-- la riga che mancava
    }
    /**
     * Chiamata dal Distraction Block quando l'utente sceglie "Continue to Sleep":
     * apre/aggiorna la sessione della notte corrente.
     */
    suspend fun recordDistractionResisted(bedtimeTargetMinutes: Int) {
        val today = LocalDate.now().toEpochDay()
        val existing = sleepSessionDao.getByDate(today)
        val session = (existing ?: SleepSessionEntity(
            dateEpochDay = today,
            bedtimeTargetMinutes = bedtimeTargetMinutes,
            actualSleepStartMinutes = null,
            wakeUpEpochMillis = null,
            distractionResisted = false,
            routineCompleted = false,
            sleepScore = null,
        )).copy(distractionResisted = true)
        sleepSessionDao.insert(session)
    }


    /** Chiamata quando l'utente completa la sleep routine (6 step). */
    suspend fun recordRoutineCompleted() {
        val today = LocalDate.now().toEpochDay()
        val existing = sleepSessionDao.getByDate(today) ?: SleepSessionEntity(
            dateEpochDay = today,
            bedtimeTargetMinutes = userPreferencesRepository.bedtimeMinutes.let { 0 },
            actualSleepStartMinutes = null,
            wakeUpEpochMillis = null,
            distractionResisted = false,
            routineCompleted = false,
            sleepScore = null,
        )
        sleepSessionDao.insert(existing.copy(routineCompleted = true))
    }

    /**
     * Chiamata dal modulo Morning Feedback al risveglio: chiude la sessione, calcola lo
     * Sleep Score, aggiorna streak e crescita della Sleep Plant.
     */
    suspend fun completeMorningFeedback(usedRelaxOrBrainDump: Boolean): Int {
        val today = LocalDate.now().toEpochDay()
        val session = sleepSessionDao.getByDate(today)

        val nightSucceeded = session?.distractionResisted == true || session?.routineCompleted == true
        val score = SleepScoreCalculator.calculate(
            wentToBedOnTime = session?.actualSleepStartMinutes != null,
            distractionResisted = session?.distractionResisted ?: false,
            routineCompleted = session?.routineCompleted ?: false,
            usedRelaxOrBrainDump = usedRelaxOrBrainDump,
        )

        if (session != null) {
            sleepSessionDao.update(
                session.copy(wakeUpEpochMillis = System.currentTimeMillis(), sleepScore = score)
            )
        }

        if (nightSucceeded) {
            userPreferencesRepository.recordSuccessfulNight(score)
        } else {
            userPreferencesRepository.recordBrokenStreak(score)
        }

        return score
    }

    /**
     * DEBUG: simula 7 notti consecutive con una qualità di sonno fissa,
     * per dimostrare rapidamente streak, sleep score e crescita della pianta.
     */
    suspend fun simulateWeek(quality: SleepQuality) {
        repeat(7) {
            when (quality) {
                SleepQuality.POOR -> {
                    val score = SleepScoreCalculator.calculate(
                        wentToBedOnTime = false,
                        distractionResisted = false,
                        routineCompleted = false,
                        usedRelaxOrBrainDump = false,
                    )
                    userPreferencesRepository.recordBrokenStreak(score)
                }
                SleepQuality.NORMAL -> {
                    val score = SleepScoreCalculator.calculate(
                        wentToBedOnTime = true,
                        distractionResisted = true,
                        routineCompleted = false,
                        usedRelaxOrBrainDump = false,
                    )
                    userPreferencesRepository.recordSuccessfulNight(score)
                }
                SleepQuality.GOOD -> {
                    val score = SleepScoreCalculator.calculate(
                        wentToBedOnTime = true,
                        distractionResisted = true,
                        routineCompleted = true,
                        usedRelaxOrBrainDump = true,
                    )
                    userPreferencesRepository.recordSuccessfulNight(score)
                }
            }
        }
    }

    suspend fun resetDebugData() {
        userPreferencesRepository.resetDebugData()
    }
}
