package com.gosleep.app.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class StreakCalculatorTest {

    @Test
    fun `successful night increments streak`() {
        assertEquals(4, StreakCalculator.nextStreak(currentStreak = 3, nightSucceeded = true))
    }

    @Test
    fun `failed night resets streak to zero`() {
        assertEquals(0, StreakCalculator.nextStreak(currentStreak = 7, nightSucceeded = false))
    }

    @Test
    fun `streak label handles zero, singular and plural`() {
        assertEquals("Start tonight", StreakCalculator.streakLabel(0))
        assertEquals("1 day in a row", StreakCalculator.streakLabel(1))
        assertEquals("5 days in a row", StreakCalculator.streakLabel(5))
    }
}
