package com.gosleep.app.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class EnergyLevelCalculatorTest {

    @Test
    fun `label thresholds match documentation states`() {
        assertEquals("Energized", EnergyLevelCalculator.label(90))
        assertEquals("Good", EnergyLevelCalculator.label(65))
        assertEquals("Tired", EnergyLevelCalculator.label(40))
        assertEquals("Exhausted", EnergyLevelCalculator.label(10))
    }

    @Test
    fun `percent is clamped between 0 and 100`() {
        assertEquals(0, EnergyLevelCalculator.percent(-5))
        assertEquals(100, EnergyLevelCalculator.percent(150))
        assertEquals(50, EnergyLevelCalculator.percent(50))
    }
}
