package com.gosleep.app.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class PlantGrowthCalculatorTest {

    @Test
    fun `success grows plant by 10 percent`() {
        assertEquals(30, PlantGrowthCalculator.nextGrowth(currentGrowthPercent = 20, nightSucceeded = true))
    }

    @Test
    fun `failure shrinks plant by 5 percent`() {
        assertEquals(15, PlantGrowthCalculator.nextGrowth(currentGrowthPercent = 20, nightSucceeded = false))
    }

    @Test
    fun `growth never exceeds 100`() {
        assertEquals(100, PlantGrowthCalculator.nextGrowth(currentGrowthPercent = 95, nightSucceeded = true))
    }

    @Test
    fun `growth never goes below 0`() {
        assertEquals(0, PlantGrowthCalculator.nextGrowth(currentGrowthPercent = 2, nightSucceeded = false))
    }

    @Test
    fun `stage thresholds match documentation states`() {
        assertEquals(PlantStage.SEED, PlantGrowthCalculator.stage(0))
        assertEquals(PlantStage.SPROUT, PlantGrowthCalculator.stage(20))
        assertEquals(PlantStage.GROWING, PlantGrowthCalculator.stage(50))
        assertEquals(PlantStage.BLOOMING, PlantGrowthCalculator.stage(90))
    }
}
