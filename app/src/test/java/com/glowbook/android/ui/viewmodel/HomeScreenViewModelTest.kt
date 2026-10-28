package com.glowbook.android.ui.viewmodel

import com.glowbook.android.domain.models.CategoryEntity
import com.glowbook.android.domain.usecase.FetchAppointmentsUpNextUseCase
import com.glowbook.android.domain.usecase.FetchCategoriesUseCase
import com.glowbook.android.domain.usecase.FetchProfessionalAvailableUseCase
import com.glowbook.android.testutils.MainDispatcherRule
import com.glowbook.android.utils.UiState
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class HomeScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fetchProfessionals: FetchProfessionalAvailableUseCase = mockk()
    private val fetchCategories: FetchCategoriesUseCase = mockk()
    private val fetchUpNext: FetchAppointmentsUpNextUseCase = mockk()

    private val viewModel = HomeScreenViewModel(fetchProfessionals, fetchCategories, fetchUpNext)

    @Test
    fun `initial states are ready`() {
        assertEquals(UiState.Ready, viewModel.professionalsState.value)
        assertEquals(UiState.Ready, viewModel.categoriesState.value)
        assertEquals(UiState.Ready, viewModel.nextAppointmentState.value)
    }

    @Test
    fun `setLoading sets professionals state to loading`() {
        viewModel.setLoading()

        assertEquals(UiState.Loading, viewModel.professionalsState.value)
    }

    @Test
    fun `fetchProfessionalAvailable requests 30 professionals and stores result`() {
        val professionals = UiState.Success(listOf(professionalEntity()))
        every { fetchProfessionals.invoke(30) } returns flowOf(professionals)

        viewModel.fetchProfessionalAvailable()

        assertEquals(professionals, viewModel.professionalsState.value)
        verify(exactly = 1) { fetchProfessionals.invoke(30) }
    }

    @Test
    fun `fetchCategories stores result`() {
        val categories = UiState.Success(listOf(CategoryEntity(1, "Cabelo")))
        every { fetchCategories.invoke() } returns flowOf(categories)

        viewModel.fetchCategories()

        assertEquals(categories, viewModel.categoriesState.value)
    }

    @Test
    fun `fetchNextAppointment stores result`() {
        val appointments = UiState.Success(listOf(appointmentEntity(1)))
        every { fetchUpNext.invoke() } returns flowOf(appointments)

        viewModel.fetchNextAppointment()

        assertEquals(appointments, viewModel.nextAppointmentState.value)
    }

    @Test
    fun `fetchHomeData loads all sections`() {
        every { fetchProfessionals.invoke(30) } returns flowOf(UiState.Success(emptyList()))
        every { fetchCategories.invoke() } returns flowOf(UiState.Error("boom"))
        every { fetchUpNext.invoke() } returns flowOf(UiState.Success(emptyList()))

        viewModel.fetchHomeData()

        assertEquals(UiState.Success(emptyList<Any>()), viewModel.professionalsState.value)
        assertEquals(UiState.Error("boom"), viewModel.categoriesState.value)
        assertEquals(UiState.Success(emptyList<Any>()), viewModel.nextAppointmentState.value)
    }

    @Test
    fun `exceptions in flows become error states`() {
        every { fetchProfessionals.invoke(30) } returns flow { throw IOException("professionals failed") }
        every { fetchCategories.invoke() } returns flow { throw IOException("categories failed") }
        every { fetchUpNext.invoke() } returns flow { throw IOException("next failed") }

        viewModel.fetchHomeData()

        assertEquals(UiState.Error("professionals failed"), viewModel.professionalsState.value)
        assertEquals(UiState.Error("categories failed"), viewModel.categoriesState.value)
        assertEquals(UiState.Error("next failed"), viewModel.nextAppointmentState.value)
    }
}
