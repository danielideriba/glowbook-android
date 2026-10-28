package com.glowbook.android.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.glowbook.android.domain.models.ProfessionalServicesEntity
import com.glowbook.android.domain.usecase.FetchProfessionalServicesUseCase
import com.glowbook.android.testutils.MainDispatcherRule
import com.glowbook.android.ui.nav.AppScreen
import com.glowbook.android.utils.UiState
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class ProfessionalServicesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fetchProfessionalServices: FetchProfessionalServicesUseCase = mockk()

    private fun viewModel(professionalId: Long? = 7) = ProfessionalServicesViewModel(
        SavedStateHandle(
            professionalId?.let { mapOf(AppScreen.ProfessionalServices.ARG_PROFESSIONAL_ID to it) }.orEmpty()
        ),
        fetchProfessionalServices
    )

    private fun loadedViewModel(vararg serviceIds: Long) = viewModel().apply {
        every { fetchProfessionalServices.invoke(7) } returns flowOf(
            UiState.Success(ProfessionalServicesEntity(professionalEntity(7), serviceIds.map { serviceEntity(it) }))
        )
        fetchProfessionalServices()
    }

    @Test
    fun `requires professional id argument`() {
        assertThrows(IllegalStateException::class.java) { viewModel(professionalId = null) }
    }

    @Test
    fun `initial state is ready with nothing selected`() {
        val viewModel = viewModel()

        assertEquals(UiState.Ready, viewModel.professionalServicesState.value)
        assertEquals(emptySet<Long>(), viewModel.selectedServiceIds.value)
    }

    @Test
    fun `fetchProfessionalServices uses professional id from navigation`() {
        val viewModel = loadedViewModel(1, 2)

        verify(exactly = 1) { fetchProfessionalServices.invoke(7) }
        assertEquals(
            UiState.Success(ProfessionalServicesEntity(professionalEntity(7), listOf(serviceEntity(1), serviceEntity(2)))),
            viewModel.professionalServicesState.value
        )
    }

    @Test
    fun `fetchProfessionalServices exception becomes error state`() {
        every { fetchProfessionalServices.invoke(7) } returns flow { throw IOException("boom") }
        val viewModel = viewModel()

        viewModel.fetchProfessionalServices()

        assertEquals(UiState.Error("boom"), viewModel.professionalServicesState.value)
    }

    @Test
    fun `toggleService selects and unselects`() {
        val viewModel = viewModel()

        viewModel.toggleService(1)
        viewModel.toggleService(2)
        assertEquals(setOf(1L, 2L), viewModel.selectedServiceIds.value)

        viewModel.toggleService(1)
        assertEquals(setOf(2L), viewModel.selectedServiceIds.value)
    }

    @Test
    fun `toggleAllServices selects every loaded service`() {
        val viewModel = loadedViewModel(1, 2, 3)
        viewModel.toggleService(2)

        viewModel.toggleAllServices()

        assertEquals(setOf(1L, 2L, 3L), viewModel.selectedServiceIds.value)
    }

    @Test
    fun `toggleAllServices clears when everything is selected`() {
        val viewModel = loadedViewModel(1, 2)
        viewModel.toggleAllServices()

        viewModel.toggleAllServices()

        assertEquals(emptySet<Long>(), viewModel.selectedServiceIds.value)
    }

    @Test
    fun `toggleAllServices does nothing useful before services load`() {
        val viewModel = viewModel()
        viewModel.toggleService(1)

        viewModel.toggleAllServices()

        assertEquals(emptySet<Long>(), viewModel.selectedServiceIds.value)
    }

    @Test
    fun `toggleAllServices with no services keeps selection empty`() {
        val viewModel = loadedViewModel()

        viewModel.toggleAllServices()

        assertEquals(emptySet<Long>(), viewModel.selectedServiceIds.value)
    }
}
