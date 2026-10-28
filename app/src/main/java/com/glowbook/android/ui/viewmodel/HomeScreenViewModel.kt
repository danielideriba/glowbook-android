package com.glowbook.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glowbook.android.domain.models.AppointmentEntity
import com.glowbook.android.domain.models.CategoryEntity
import com.glowbook.android.domain.models.ProfessionalEntity
import com.glowbook.android.domain.usecase.FetchCategoriesUseCase
import com.glowbook.android.domain.usecase.FetchAppointmentsUpNextUseCase
import com.glowbook.android.domain.usecase.FetchProfessionalAvailableUseCase
import com.glowbook.android.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val fetchProfessionalAvailableUseCase: FetchProfessionalAvailableUseCase,
    private val fetchCategoriesUseCase: FetchCategoriesUseCase,
    private val fetchAppointmentsUpNextUseCase: FetchAppointmentsUpNextUseCase
) : ViewModel() {
    private val _professionalsState = MutableStateFlow<UiState<List<ProfessionalEntity>>>(UiState.Ready)
    val professionalsState: StateFlow<UiState<List<ProfessionalEntity>>> = _professionalsState.asStateFlow()

    private val _categoriesState = MutableStateFlow<UiState<List<CategoryEntity>>>(UiState.Ready)
    val categoriesState: StateFlow<UiState<List<CategoryEntity>>> = _categoriesState.asStateFlow()

    private val _nextAppointmentState = MutableStateFlow<UiState<List<AppointmentEntity>>>(UiState.Ready)
    val nextAppointmentState: StateFlow<UiState<List<AppointmentEntity>>> = _nextAppointmentState.asStateFlow()

    fun setLoading() {
        _professionalsState.value = UiState.Loading
    }

    fun fetchHomeData() {
        fetchNextAppointment()
        fetchCategories()
        fetchProfessionalAvailable()
    }

    fun fetchProfessionalAvailable() {
        viewModelScope.launch {
            fetchProfessionalAvailableUseCase.invoke(30)
                .catch { error -> _professionalsState.value = UiState.Error("${error.localizedMessage}") }
                .collect { state ->
                    _professionalsState.value = state
                }
        }
    }

    fun fetchCategories() {
        viewModelScope.launch {
            fetchCategoriesUseCase.invoke()
                .catch { error -> _categoriesState.value = UiState.Error("${error.localizedMessage}") }
                .collect { state ->
                    _categoriesState.value = state
                }
        }
    }

    fun fetchNextAppointment() {
        viewModelScope.launch {
            fetchAppointmentsUpNextUseCase.invoke()
                .catch { error -> _nextAppointmentState.value = UiState.Error("${error.localizedMessage}") }
                .collect { state ->
                    _nextAppointmentState.value = state
                }
        }
    }
}
