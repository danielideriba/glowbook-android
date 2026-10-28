package com.glowbook.android.domain.usecase

import com.glowbook.android.data.remote.model.CategoriesResponses
import com.glowbook.android.domain.models.CategoryEntity
import com.glowbook.android.domain.repository.CategoriesRepository
import com.glowbook.android.utils.ApiState
import com.glowbook.android.utils.UiState
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class FetchCategoriesUseCaseTest {

    private val repository: CategoriesRepository = mockk()
    private val useCase = FetchCategoriesUseCase(repository)

    @Test
    fun `maps categories to entities`() = runTest {
        every { repository.fetchCategories() } returns flowOf(
            ApiState.Success(listOf(CategoriesResponses(1, "Cabelo"), CategoriesResponses(2, "Unhas")))
        )

        val emissions = useCase.invoke().toList()

        assertEquals(
            listOf(UiState.Success(listOf(CategoryEntity(1, "Cabelo"), CategoryEntity(2, "Unhas")))),
            emissions
        )
    }

    @Test
    fun `maps not found error to friendly message`() = runTest {
        every { repository.fetchCategories() } returns flowOf(ApiState.Error("Not found", 404))

        val result = useCase.invoke().toList().single()

        assertEquals(UiState.Error(NOT_FOUND_MESSAGE), result)
    }

    @Test
    fun `keeps other error messages`() = runTest {
        every { repository.fetchCategories() } returns flowOf(ApiState.Error("Sem conexão com a internet", -1))

        val result = useCase.invoke().toList().single()

        assertEquals(UiState.Error("Sem conexão com a internet"), result)
    }
}

internal const val NOT_FOUND_MESSAGE = "Resources not found. Please check the spelling and try again."
