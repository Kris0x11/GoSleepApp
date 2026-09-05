package com.gosleep.app.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SleepScoreCalculatorTest {

    @Test
    fun `all positive factors give maximum score`() {
        val score = SleepScoreCalculator.calculate(
            wentToBedOnTime = true,
            distractionResisted = true,
            routineCompleted = true,
            usedRelaxOrBrainDump = true,
        )
        assertEquals(100, score)
    }

    @Test
    fun `no positive factors give base score only`() {
        val score = SleepScoreCalculator.calculate(
            wentToBedOnTime = false,
            distractionResisted = false,
            routineCompleted = false,
            usedRelaxOrBrainDump = false,
        )
        assertEquals(40, score)
    }

    @Test
    fun `resisting distraction weighs more than completing routine`() {
        val distractionOnly = SleepScoreCalculator.calculate(
            wentToBedOnTime = false,
            distractionResisted = true,
            routineCompleted = false,
            usedRelaxOrBrainDump = false,
        )
        val routineOnly = SleepScoreCalculator.calculate(
            wentToBedOnTime = false,
            distractionResisted = false,
            routineCompleted = true,
            usedRelaxOrBrainDump = false,
        )
        assertTrue(distractionOnly > routineOnly)
    }

    @Test
    fun `score is always clamped between 0 and 100`() {
        val score = SleepScoreCalculator.calculate(
            wentToBedOnTime = true,
            distractionResisted = true,
            routineCompleted = true,
            usedRelaxOrBrainDump = true,
        )
        assertTrue(score in 0..100)
    }

    @Test
    fun `motivational label matches score bands`() {
        assertEquals("Great Night!", SleepScoreCalculator.motivationalLabel(90))
        assertEquals("Good Progress", SleepScoreCalculator.motivationalLabel(65))
        assertEquals("Keep Trying", SleepScoreCalculator.motivationalLabel(40))
    }
}
