package com.glowbook.android.domain.usecase

import com.glowbook.android.data.remote.model.ProfessionalWithServices
import com.glowbook.android.data.repository.professional
import com.glowbook.android.data.repository.service
import com.glowbook.android.domain.models.ProfessionalEntity
import com.glowbook.android.domain.models.ProfessionalServicesEntity
import com.glowbook.android.domain.models.ServiceEntity
import com.glowbook.android.domain.repository.ProfessionalServicesRepository
import com.glowbook.android.utils.ApiState
import com.glowbook.android.utils.UiState
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class FetchProfessionalServicesUseCaseTest {

    private val repository: ProfessionalServicesRepository = mockk()
    private val useCase = FetchProfessionalServicesUseCase(repository)

    @Test
    fun `maps professional and services to entities`() = runTest {
        every { repository.fetchProfessionalServices(1) } returns flowOf(
            ApiState.Success(
                ProfessionalWithServices(
                    professional(1).copy(avatar = "👩", corFundo = "#FDE8EC"),
                    listOf(
                        service(1, "Corte").copy(preco = 120.0, duracaoMinutos = 45, icone = "scissors"),
                        service(2, "Escova").copy(preco = null, duracaoMinutos = null, icone = null)
                    )
                )
            )
        )

        val result = useCase.invoke(1).toList().single()

        assertEquals(
            UiState.Success(
                ProfessionalServicesEntity(
                    professional = ProfessionalEntity(1, "Camila Rocha", "Cabeleireira", 4.9, "👩", "#FDE8EC", true, 1),
                    services = listOf(
                        ServiceEntity(1, "Corte", 120.0, 45, "scissors", 1),
                        ServiceEntity(2, "Escova", 0.0, 0, null, 1)
                    )
                )
            ),
            result
        )
    }

    @Test
    fun `maps professional not found to friendly message`() = runTest {
        every { repository.fetchProfessionalServices(99) } returns flowOf(ApiState.Error("Professional 99 not found"))

        assertEquals(UiState.Error(NOT_FOUND_MESSAGE), useCase.invoke(99).toList().single())
    }

    @Test
    fun `keeps other error messages`() = runTest {
        every { repository.fetchProfessionalServices(1) } returns flowOf(ApiState.Error("timeout", -1))

        assertEquals(UiState.Error("timeout"), useCase.invoke(1).toList().single())
    }
}
