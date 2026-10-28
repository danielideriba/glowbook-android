package com.glowbook.android.ui.viewmodel

import com.glowbook.android.domain.usecase.FetchAppointmentsCancelledUseCase
import com.glowbook.android.domain.usecase.FetchAppointmentsHistoryUseCase
import com.glowbook.android.domain.usecase.FetchAppointmentsUpNextUseCase
import com.glowbook.android.testutils.MainDispatcherRule
import com.glowbook.android.utils.UiState
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class AppointmentsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fetchHistory: FetchAppointmentsHistoryUseCase = mockk()
    private val fetchCancelled: FetchAppointmentsCancelledUseCase = mockk()
    private val fetchUpNext: FetchAppointmentsUpNextUseCase = mockk()

    private val viewModel = AppointmentsViewModel(fetchHistory, fetchCancelled, fetchUpNext)

    @Test
    fun `initial states are ready`() {
        assertEquals(UiState.Ready, viewModel.appointmentsUpNextState.value)
        assertEquals(UiState.Ready, viewModel.appointmentsHistoryState.value)
        assertEquals(UiState.Ready, viewModel.cancelledState.value)
    }

    @Test
    fun `fetchAppointmentsUpNext stores result`() {
        val state = UiState.Success(listOf(appointmentEntity(1)))
        every { fetchUpNext.invoke() } returns flowOf(state)

        viewModel.fetchAppointmentsUpNext()

        assertEquals(state, viewModel.appointmentsUpNextState.value)
    }

    @Test
    fun `fetchAppointmentsHistory stores result`() {
        val state = UiState.Success(listOf(appointmentEntity(2, status = "concluido")))
        every { fetchHistory.invoke() } returns flowOf(state)

        viewModel.fetchAppointmentsHistory()

        assertEquals(state, viewModel.appointmentsHistoryState.value)
    }

    @Test
    fun `fetchAppointmentsCancelled stores result`() {
        val state = UiState.Success(listOf(appointmentEntity(3, status = "cancelado")))
        every { fetchCancelled.invoke() } returns flowOf(state)

        viewModel.fetchAppointmentsCancelled()

        assertEquals(state, viewModel.cancelledState.value)
    }

    @Test
    fun `each tab keeps its own state`() {
        every { fetchUpNext.invoke() } returns flowOf(UiState.Success(emptyList()))
        every { fetchHistory.invoke() } returns flowOf(UiState.Error("history failed"))
        every { fetchCancelled.invoke() } returns flowOf(UiState.Loading)

        viewModel.fetchAppointmentsUpNext()
        viewModel.fetchAppointmentsHistory()
        viewModel.fetchAppointmentsCancelled()

        assertEquals(UiState.Success(emptyList<Any>()), viewModel.appointmentsUpNextState.value)
        assertEquals(UiState.Error("history failed"), viewModel.appointmentsHistoryState.value)
        assertEquals(UiState.Loading, viewModel.cancelledState.value)
    }

    @Test
    fun `exceptions in flows become error states`() {
        every { fetchUpNext.invoke() } returns flow { throw IOException("up next failed") }
        every { fetchHistory.invoke() } returns flow { throw IOException("history failed") }
        every { fetchCancelled.invoke() } returns flow { throw IOException("cancelled failed") }

        viewModel.fetchAppointmentsUpNext()
        viewModel.fetchAppointmentsHistory()
        viewModel.fetchAppointmentsCancelled()

        assertEquals(UiState.Error("up next failed"), viewModel.appointmentsUpNextState.value)
        assertEquals(UiState.Error("history failed"), viewModel.appointmentsHistoryState.value)
        assertEquals(UiState.Error("cancelled failed"), viewModel.cancelledState.value)
    }
}
