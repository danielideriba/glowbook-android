package com.glowbook.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glowbook.android.domain.models.AppointmentEntity
import com.glowbook.android.domain.usecase.FetchAppointmentsCancelledUseCase
import com.glowbook.android.domain.usecase.FetchAppointmentsHistoryUseCase
import com.glowbook.android.domain.usecase.FetchAppointmentsUpNextUseCase
import com.glowbook.android.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppointmentsViewModel @Inject constructor(
    private val fetchAppointmentsHistoryUseCase: FetchAppointmentsHistoryUseCase,
    private val fetchAppointmentsCancelledUseCase: FetchAppointmentsCancelledUseCase,
    private val fetchAppointmentsUpNextUseCase: FetchAppointmentsUpNextUseCase
) : ViewModel() {
    private val _appointmentsUpNextState = MutableStateFlow<UiState<List<AppointmentEntity>>>(UiState.Ready)
    val appointmentsUpNextState: StateFlow<UiState<List<AppointmentEntity>>> = _appointmentsUpNextState.asStateFlow()

    private val _appointmentsHistoryState = MutableStateFlow<UiState<List<AppointmentEntity>>>(UiState.Ready)
    val appointmentsHistoryState: StateFlow<UiState<List<AppointmentEntity>>> = _appointmentsHistoryState.asStateFlow()

    private val _cancelledState = MutableStateFlow<UiState<List<AppointmentEntity>>>(UiState.Ready)
    val cancelledState: StateFlow<UiState<List<AppointmentEntity>>> = _cancelledState.asStateFlow()

    fun fetchAppointmentsUpNext() {
        viewModelScope.launch {
            fetchAppointmentsUpNextUseCase.invoke()
                .catch { error -> _appointmentsUpNextState.value = UiState.Error("${error.localizedMessage}") }
                .collect { state ->
                    _appointmentsUpNextState.value = state
                }
        }
    }

    fun fetchAppointmentsHistory() {
        viewModelScope.launch {
            fetchAppointmentsHistoryUseCase.invoke()
                .catch { error -> _appointmentsHistoryState.value = UiState.Error("${error.localizedMessage}") }
                .collect { state ->
                    _appointmentsHistoryState.value = state
                }
        }
    }

    fun fetchAppointmentsCancelled() {
        viewModelScope.launch {
            fetchAppointmentsCancelledUseCase.invoke()
                .catch { error -> _cancelledState.value = UiState.Error("${error.localizedMessage}") }
                .collect { state ->
                    _cancelledState.value = state
                }
        }
    }
}
