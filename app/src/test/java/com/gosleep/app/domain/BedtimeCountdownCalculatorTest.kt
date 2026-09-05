package com.gosleep.app.domain

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalTime

class BedtimeCountdownCalculatorTest {

    @Test
    fun `countdown to bedtime later today`() {
        val now = LocalTime.of(17, 34)
        val bedtime = 22 * 60 + 30 // 22:30

        val countdown = BedtimeCountdownCalculator.countdown(bedtime, now)

        assertEquals(4, countdown.hours)
        assertEquals(56, countdown.minutes)
    }

    @Test
    fun `countdown wraps to next day when bedtime already passed`() {
        val now = LocalTime.of(23, 0)
        val bedtime = 22 * 60 + 30 // already passed

        val countdown = BedtimeCountdownCalculator.countdown(bedtime, now)

        assertEquals(23, countdown.hours)
        assertEquals(30, countdown.minutes)
    }

    @Test
    fun `countdown label formats without hours when under one hour`() {
        val now = LocalTime.of(22, 15)
        val bedtime = 22 * 60 + 30

        val countdown = BedtimeCountdownCalculator.countdown(bedtime, now)

        assertEquals("15m", countdown.label())
    }
}
