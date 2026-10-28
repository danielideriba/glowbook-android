package com.glowbook.android.domain.usecase

import com.glowbook.android.data.remote.model.ProfessionalsResponse
import com.glowbook.android.domain.models.ProfessionalEntity
import com.glowbook.android.domain.repository.ProfessionalsRepository
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

class FetchProfessionalAvailableUseCaseTest {

    private val repository: ProfessionalsRepository = mockk()
    private val useCase = FetchProfessionalAvailableUseCase(repository)

    @Test
    fun `maps professionals to entities`() = runTest {
        every { repository.fetchProfessionalsAvailable(30) } returns flowOf(
            ApiState.Success(listOf(ProfessionalsResponse(1, "Camila Rocha", "Cabeleireira", 4.9, "👩", "#FDE8EC", true, 2)))
        )

        val result = useCase.invoke(30).toList().single()

        assertEquals(
            UiState.Success(listOf(ProfessionalEntity(1, "Camila Rocha", "Cabeleireira", 4.9, "👩", "#FDE8EC", true, 2))),
            result
        )
    }

    @Test
    fun `passes limit to repository`() = runTest {
        every { repository.fetchProfessionalsAvailable(any()) } returns flowOf(ApiState.Success(emptyList()))

        useCase.invoke(5).toList()

        verify(exactly = 1) { repository.fetchProfessionalsAvailable(5) }
    }

    @Test
    fun `maps not found error to friendly message`() = runTest {
        every { repository.fetchProfessionalsAvailable(30) } returns flowOf(ApiState.Error("NOT FOUND", 404))

        assertEquals(UiState.Error(NOT_FOUND_MESSAGE), useCase.invoke(30).toList().single())
    }

    @Test
    fun `keeps other error messages`() = runTest {
        every { repository.fetchProfessionalsAvailable(30) } returns flowOf(ApiState.Error("timeout", -1))

        assertEquals(UiState.Error("timeout"), useCase.invoke(30).toList().single())
    }
}
