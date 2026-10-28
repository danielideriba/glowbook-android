package com.glowbook.android.domain.usecase

import com.glowbook.android.data.remote.model.ProfessionalCalendarResponse
import com.glowbook.android.data.repository.booking
import com.glowbook.android.data.repository.professional
import com.glowbook.android.data.repository.service
import com.glowbook.android.domain.models.BookedSlotEntity
import com.glowbook.android.domain.models.ProfessionalCalendarEntity
import com.glowbook.android.domain.models.ProfessionalEntity
import com.glowbook.android.domain.models.ServiceEntity
import com.glowbook.android.domain.repository.ProfessionalCalendarRepository
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

class FetchProfessionalCalendarUseCaseTest {

    private val repository: ProfessionalCalendarRepository = mockk()
    private val useCase = FetchProfessionalCalendarUseCase(repository)

    private val start = LocalDate.of(2026, 10, 27)
    private val end = LocalDate.of(2026, 11, 2)

    @Test
    fun `passes dates to repository as iso strings`() = runTest {
        every { repository.fetchProfessionalCalendar(any(), any(), any(), any()) } returns
            flowOf(ApiState.Error("boom"))

        useCase.invoke(1, listOf(1, 2), start, end).toList()

        verify(exactly = 1) { repository.fetchProfessionalCalendar(1, listOf(1, 2), "2026-10-27", "2026-11-02") }
    }

    @Test
    fun `maps calendar to entity with booked slots`() = runTest {
        every { repository.fetchProfessionalCalendar(1, listOf(1), "2026-10-27", "2026-11-02") } returns flowOf(
            ApiState.Success(
                ProfessionalCalendarResponse(
                    professional = professional(1),
                    services = listOf(
                        service(1, "Corte").copy(preco = 120.0, duracaoMinutos = 45, icone = "scissors"),
                        service(2, "Escova").copy(preco = null, duracaoMinutos = null)
                    ),
                    appointments = listOf(booking(10, "11:00:00"), booking(11, "15:30:00"))
                )
            )
        )

        val result = useCase.invoke(1, listOf(1), start, end).toList().single()

        assertEquals(
            UiState.Success(
                ProfessionalCalendarEntity(
                    professional = ProfessionalEntity(1, "Camila Rocha", "Cabeleireira", 4.9, null, null, true, 1),
                    services = listOf(
                        ServiceEntity(1, "Corte", 120.0, 45, "scissors", 1),
                        ServiceEntity(2, "Escova", 0.0, 0, null, 1)
                    ),
                    bookedSlots = listOf(
                        BookedSlotEntity(LocalDate.of(2026, 10, 30), LocalTime.of(11, 0)),
                        BookedSlotEntity(LocalDate.of(2026, 10, 30), LocalTime.of(15, 30))
                    )
                )
            ),
            result
        )
    }

    @Test
    fun `maps professional not found to friendly message`() = runTest {
        every { repository.fetchProfessionalCalendar(any(), any(), any(), any()) } returns
            flowOf(ApiState.Error("Professional 1 not found"))

        assertEquals(UiState.Error(NOT_FOUND_MESSAGE), useCase.invoke(1, emptyList(), start, end).toList().single())
    }

    @Test
    fun `keeps other error messages`() = runTest {
        every { repository.fetchProfessionalCalendar(any(), any(), any(), any()) } returns
            flowOf(ApiState.Error("Sem conexão com a internet", -1))

        assertEquals(
            UiState.Error("Sem conexão com a internet"),
            useCase.invoke(1, emptyList(), start, end).toList().single()
        )
    }
}
