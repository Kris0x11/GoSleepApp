package com.gosleep.app.navigation

/**
 * Rotte dell'app, ricalcano la Site-Map del documento:
 * ONBOARDING -> DASHBOARD -> { NIGHT_MODE (BRAIN_DUMP, ROUTINE_FLOW), RELAX_MODE }
 * ONLY WHEN TRIGGERED -> DISTRACTION_BLOCK, MORNING_FEEDBACK
 */
object Routes {
    const val ONBOARDING = "onboarding"
    const val DASHBOARD = "dashboard"
    const val SLEEP_PREPARATION = "sleep_preparation"
    const val NIGHT_MODE_INTRO = "night_mode_intro"
    const val BRAIN_DUMP = "brain_dump"
    const val ROUTINE_FLOW = "routine_flow"
    const val RELAX_MODE = "relax_mode"
    const val DISTRACTION_BLOCK = "distraction_block"
    const val MORNING_FEEDBACK = "morning_feedback"
}
