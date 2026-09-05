package com.gosleep.app.ui.distraction

import com.gosleep.app.data.repository.SleepRepository
import io.mockk.coEvery
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

/**
 * Verifica il "forced friction" descritto nel documento: il tasto "I choose to stay awake"
 * deve restare nascosto per esattamente 5 secondi dopo la comparsa della schermata.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DistractionBlockViewModelTest {

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
    fun `stay awake button is hidden immediately after creation`() = runTest {
        val viewModel = DistractionBlockViewModel(sleepRepository)
        assertFalse(viewModel.uiState.value.stayAwakeButtonVisible)
        assertEquals(5, viewModel.uiState.value.secondsRemaining)
    }

    @Test
    fun `stay awake button becomes visible only after 5 seconds`() = runTest {
        val viewModel = DistractionBlockViewModel(sleepRepository)

        dispatcher.scheduler.advanceTimeBy(4_900)
        assertFalse(viewModel.uiState.value.stayAwakeButtonVisible)

        dispatcher.scheduler.advanceTimeBy(200)
        assertTrue(viewModel.uiState.value.stayAwakeButtonVisible)
    }

    @Test
    fun `choosing stay awake before timer ends is a no-op`() = runTest {
        val viewModel = DistractionBlockViewModel(sleepRepository)

        dispatcher.scheduler.advanceTimeBy(1_000)
        viewModel.onChooseToStayAwake()

        assertEquals(null, viewModel.uiState.value.resolution)
    }

    @Test
    fun `continue to sleep resolves immediately and records the night`() = runTest {
        coEvery { sleepRepository.recordDistractionResisted(any()) } returns Unit
        val viewModel = DistractionBlockViewModel(sleepRepository)

        var callbackInvoked = false
        viewModel.onContinueToSleep(bedtimeTargetMinutes = 1350) { callbackInvoked = true }
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(DistractionBlockUiState.Resolution.SLEEP_NOW, viewModel.uiState.value.resolution)
        assertTrue(callbackInvoked)
    }
}
