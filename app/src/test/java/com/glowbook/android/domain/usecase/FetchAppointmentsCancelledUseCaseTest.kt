package com.glowbook.android.domain.usecase

import com.glowbook.android.data.remote.model.AppointmentHistoryWithServices
import com.glowbook.android.data.repository.historyRow
import com.glowbook.android.data.repository.service
import com.glowbook.android.domain.models.AppointmentEntity
import com.glowbook.android.domain.repository.AppointmentsCancelledRepository
import com.glowbook.android.utils.ApiState
import com.glowbook.android.utils.UiState
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class FetchAppointmentsCancelledUseCaseTest {

    private val repository: AppointmentsCancelledRepository = mockk()
    private val useCase = FetchAppointmentsCancelledUseCase(repository)

    @Test
    fun `maps appointment with services into entity`() = runTest {
        every { repository.fetchAppointmentsCancelled() } returns flowOf(
            ApiState.Success(
                listOf(
                    AppointmentHistoryWithServices(
                        historyRow(id = 8, idServicos = 1, status = "cancelado"),
                        listOf(
                            service(1, "Corte").copy(preco = 120.0, duracaoMinutos = 45),
                            service(2, "Escova").copy(preco = 140.5, duracaoMinutos = 90)
                        )
                    )
                )
            )
        )

        val result = useCase.invoke().toList().single()

        assertEquals(
            UiState.Success(
                listOf(
                    AppointmentEntity(
                        id = 8,
                        services = "Corte + Escova",
                        price = 260.5,
                        durationMinutes = 135,
                        date = LocalDate.of(2026, 10, 20),
                        time = LocalTime.of(10, 0),
                        status = "cancelado",
                        professionalId = 1,
                        professionalName = "Camila Rocha",
                        professionalAvatar = null,
                        professionalRating = 4.9,
                        category = "Cabelo",
                        isPast = true
                    )
                )
            ),
            result
        )
    }

    @Test
    fun `copies isPast flag from api`() = runTest {
        every { repository.fetchAppointmentsCancelled() } returns flowOf(
            ApiState.Success(
                listOf(AppointmentHistoryWithServices(historyRow(id = 8, idServicos = null).copy(jaPassou = false), emptyList()))
            )
        )

        val entity = (useCase.invoke().toList().single() as UiState.Success).data.single()

        assertFalse(entity.isPast)
        assertEquals("", entity.services)
    }

    @Test
    fun `treats missing price and duration as zero`() = runTest {
        every { repository.fetchAppointmentsCancelled() } returns flowOf(
            ApiState.Success(
                listOf(
                    AppointmentHistoryWithServices(
                        historyRow(id = 8, idServicos = 1),
                        listOf(service(1).copy(preco = null, duracaoMinutos = null))
                    )
                )
            )
        )

        val entity = (useCase.invoke().toList().single() as UiState.Success).data.single()

        assertEquals(0.0, entity.price!!, 0.0)
        assertEquals(0, entity.durationMinutes)
    }

    @Test
    fun `maps not found error to friendly message`() = runTest {
        every { repository.fetchAppointmentsCancelled() } returns flowOf(ApiState.Error("Not found", 404))

        assertEquals(UiState.Error(NOT_FOUND_MESSAGE), useCase.invoke().toList().single())
    }

    @Test
    fun `keeps other error messages`() = runTest {
        every { repository.fetchAppointmentsCancelled() } returns flowOf(ApiState.Error("Response.error()", 500))

        assertEquals(UiState.Error("Response.error()"), useCase.invoke().toList().single())
    }
}
