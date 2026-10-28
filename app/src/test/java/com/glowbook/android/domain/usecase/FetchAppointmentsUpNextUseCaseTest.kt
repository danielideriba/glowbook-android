package com.glowbook.android.domain.usecase

import com.glowbook.android.data.remote.model.AppointmentUpNextWithServices
import com.glowbook.android.data.repository.service
import com.glowbook.android.data.repository.upNextRow
import com.glowbook.android.domain.models.AppointmentEntity
import com.glowbook.android.domain.repository.AppointmentsUpNextRepository
import com.glowbook.android.utils.ApiState
import com.glowbook.android.utils.UiState
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class FetchAppointmentsUpNextUseCaseTest {

    private val repository: AppointmentsUpNextRepository = mockk()
    private val useCase = FetchAppointmentsUpNextUseCase(repository)

    @Test
    fun `maps appointment with services into entity summing price and duration`() = runTest {
        every { repository.fetchAppointmentsUpNext() } returns flowOf(
            ApiState.Success(
                listOf(
                    AppointmentUpNextWithServices(
                        upNextRow(id = 5, idServicos = 1),
                        listOf(
                            service(1, "Corte").copy(preco = 120.0, duracaoMinutos = 45),
                            service(2, "Escova").copy(preco = 140.0, duracaoMinutos = 90)
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
                        id = 5,
                        services = "Corte + Escova",
                        price = 260.0,
                        durationMinutes = 135,
                        date = LocalDate.of(2026, 10, 30),
                        time = LocalTime.of(14, 0),
                        status = "confirmado",
                        professionalId = 1,
                        professionalName = "Camila Rocha",
                        professionalAvatar = null,
                        professionalRating = 4.9,
                        category = "Cabelo",
                        isPast = false
                    )
                )
            ),
            result
        )
    }

    @Test
    fun `treats missing price and duration as zero`() = runTest {
        every { repository.fetchAppointmentsUpNext() } returns flowOf(
            ApiState.Success(
                listOf(
                    AppointmentUpNextWithServices(
                        upNextRow(id = 5, idServicos = 1),
                        listOf(service(1, "Corte").copy(preco = null, duracaoMinutos = null))
                    )
                )
            )
        )

        val entity = (useCase.invoke().toList().single() as UiState.Success).data.single()

        assertEquals(0.0, entity.price!!, 0.0)
        assertEquals(0, entity.durationMinutes)
    }

    @Test
    fun `maps appointment without services to empty description`() = runTest {
        every { repository.fetchAppointmentsUpNext() } returns flowOf(
            ApiState.Success(listOf(AppointmentUpNextWithServices(upNextRow(id = 5, idServicos = 1), emptyList())))
        )

        val entity = (useCase.invoke().toList().single() as UiState.Success).data.single()

        assertEquals("", entity.services)
        assertEquals(0.0, entity.price!!, 0.0)
    }

    @Test
    fun `maps not found error to friendly message`() = runTest {
        every { repository.fetchAppointmentsUpNext() } returns flowOf(ApiState.Error("Not found", 404))

        assertEquals(UiState.Error(NOT_FOUND_MESSAGE), useCase.invoke().toList().single())
    }

    @Test
    fun `keeps other error messages`() = runTest {
        every { repository.fetchAppointmentsUpNext() } returns flowOf(ApiState.Error("Empty body"))

        assertEquals(UiState.Error("Empty body"), useCase.invoke().toList().single())
    }
}
