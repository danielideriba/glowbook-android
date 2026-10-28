package com.glowbook.android.domain.usecase

import com.glowbook.android.data.remote.model.CreateAppointmentRequest
import com.glowbook.android.domain.repository.CreateAppointmentRepository
import com.glowbook.android.utils.ApiState
import com.glowbook.android.utils.UiState
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class CreateAppointmentUseCaseTest {

    private val repository: CreateAppointmentRepository = mockk()
    private val useCase = CreateAppointmentUseCase(repository)

    private val date = LocalDate.of(2026, 10, 30)
    private val time = LocalTime.of(14, 0)

    @Test
    fun `builds rpc request from date time professional and services`() = runTest {
        every { repository.createAppointment(any()) } returns flowOf(ApiState.Success(42L))

        useCase.invoke(1, listOf(1, 2), date, time).toList()

        verify(exactly = 1) {
            repository.createAppointment(CreateAppointmentRequest("2026-10-30", "14:00:00", 1, listOf(1, 2)))
        }
    }

    @Test
    fun `emits loading then created id`() = runTest {
        every { repository.createAppointment(any()) } returns flowOf(ApiState.Success(42L))

        val emissions = useCase.invoke(1, listOf(1), date, time).toList()

        assertEquals(listOf(UiState.Loading, UiState.Success(42L)), emissions)
    }

    @Test
    fun `emits loading then not found message`() = runTest {
        every { repository.createAppointment(any()) } returns flowOf(ApiState.Error("Not found", 404))

        val emissions = useCase.invoke(1, listOf(1), date, time).toList()

        assertEquals(listOf(UiState.Loading, UiState.Error(NOT_FOUND_MESSAGE)), emissions)
    }

    @Test
    fun `emits loading then original error message`() = runTest {
        every { repository.createAppointment(any()) } returns flowOf(ApiState.Error("Response.error()", 400))

        val emissions = useCase.invoke(1, listOf(1), date, time).toList()

        assertEquals(listOf(UiState.Loading, UiState.Error("Response.error()")), emissions)
    }
}
