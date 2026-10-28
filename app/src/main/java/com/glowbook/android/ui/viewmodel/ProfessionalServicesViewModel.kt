package com.glowbook.android.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glowbook.android.domain.models.ProfessionalServicesEntity
import com.glowbook.android.domain.usecase.FetchProfessionalServicesUseCase
import com.glowbook.android.ui.nav.AppScreen
import com.glowbook.android.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfessionalServicesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val fetchProfessionalServicesUseCase: FetchProfessionalServicesUseCase
) : ViewModel() {
    private val professionalId: Long = checkNotNull(savedStateHandle[AppScreen.ProfessionalServices.ARG_PROFESSIONAL_ID])

    private val _professionalServicesState = MutableStateFlow<UiState<ProfessionalServicesEntity>>(UiState.Ready)
    val professionalServicesState: StateFlow<UiState<ProfessionalServicesEntity>> = _professionalServicesState.asStateFlow()

    private val _selectedServiceIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedServiceIds: StateFlow<Set<Long>> = _selectedServiceIds.asStateFlow()

    fun fetchProfessionalServices() {
        viewModelScope.launch {
            fetchProfessionalServicesUseCase.invoke(professionalId)
                .catch { error -> _professionalServicesState.value = UiState.Error("${error.localizedMessage}") }
                .collect { state ->
                    _professionalServicesState.value = state
                }
        }
    }

    fun toggleService(serviceId: Long) {
        _selectedServiceIds.update { selected ->
            if (serviceId in selected) selected - serviceId else selected + serviceId
        }
    }

    fun toggleAllServices() {
        val allIds = (_professionalServicesState.value as? UiState.Success)
            ?.data?.services?.map { it.id }?.toSet()
            .orEmpty()
        _selectedServiceIds.update { selected ->
            if (allIds.isNotEmpty() && selected.containsAll(allIds)) emptySet() else allIds
        }
    }
}
