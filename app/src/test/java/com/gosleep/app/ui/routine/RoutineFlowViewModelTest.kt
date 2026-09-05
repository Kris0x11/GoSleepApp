package com.gosleep.app.ui.routine

import com.gosleep.app.data.repository.SleepRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RoutineFlowViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var sleepRepository: SleepRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        sleepRepository = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `starts on first of six steps`() {
        val viewModel = RoutineFlowViewModel(sleepRepository)
        assertEquals(1, viewModel.uiState.value.stepNumber)
        assertEquals("charge_phone", viewModel.uiState.value.currentStep.id)
    }

    @Test
    fun `advances one step at a time on step done`() {
        val viewModel = RoutineFlowViewModel(sleepRepository)
        viewModel.onStepDone()
        assertEquals(2, viewModel.uiState.value.stepNumber)
        assertEquals("screens_off", viewModel.uiState.value.currentStep.id)
    }

    @Test
    fun `completes after all six steps and records completion`() = runTest {
        coEvery { sleepRepository.recordRoutineCompleted() } returns Unit
        val viewModel = RoutineFlowViewModel(sleepRepository)

        repeat(ROUTINE_STEPS.size) { viewModel.onStepDone() }
        dispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.completed)
        coVerify(exactly = 1) { sleepRepository.recordRoutineCompleted() }
    }

    @Test
    fun `does not advance past the last step once completed`() {
        val viewModel = RoutineFlowViewModel(sleepRepository)
        repeat(ROUTINE_STEPS.size + 3) { viewModel.onStepDone() }
        assertTrue(viewModel.uiState.value.completed)
    }

    @Test
    fun `skip routine marks completed without recording it as finished`() {
        val viewModel = RoutineFlowViewModel(sleepRepository)
        viewModel.onSkipRoutine()
        assertTrue(viewModel.uiState.value.completed)
        assertFalse(viewModel.uiState.value.stepNumber == ROUTINE_STEPS.size + 1)
    }
}
