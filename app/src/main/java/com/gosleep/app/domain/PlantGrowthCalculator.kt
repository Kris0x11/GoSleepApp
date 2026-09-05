package com.gosleep.app.domain

/**
 * Logica della "Sleep Plant": elemento di gamification
 * (dashboard -> "Sleep Plant", morning feedback -> "Your Plant Grew! +10% Growth").
 */
object PlantGrowthCalculator {

    private const val GROWTH_PER_SUCCESS = 10
    private const val DECAY_PER_FAILURE = 5

    fun nextGrowth(currentGrowthPercent: Int, nightSucceeded: Boolean): Int {
        val delta = if (nightSucceeded) GROWTH_PER_SUCCESS else -DECAY_PER_FAILURE
        return (currentGrowthPercent + delta).coerceIn(0, 100)
    }

    /** Stadio testuale/emoji della pianta, usato per scegliere l'illustrazione in UI. */
    fun stage(growthPercent: Int): PlantStage = when {
        growthPercent >= 80 -> PlantStage.BLOOMING
        growthPercent >= 40 -> PlantStage.GROWING
        growthPercent > 0 -> PlantStage.SPROUT
        else -> PlantStage.SEED
    }

    /** Nome mostrato in dashboard, es. "Seedling" nel mock di riferimento. */
    fun stageLabel(stage: PlantStage): String = when (stage) {
        PlantStage.SEED -> "Seed"
        PlantStage.SPROUT -> "Seedling"
        PlantStage.GROWING -> "Sprouting"
        PlantStage.BLOOMING -> "In Bloom"
    }
}

enum class PlantStage { SEED, SPROUT, GROWING, BLOOMING }
