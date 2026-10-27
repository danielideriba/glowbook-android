package com.glowbook.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glowbook.android.domain.entity.ProfessionalEntity
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
    private val fetchProfessionalAvailableUseCase: FetchProfessionalAvailableUseCase
) : ViewModel() {
    private val _professionalsState = MutableStateFlow<UiState<List<ProfessionalEntity>>>(UiState.Ready)
    val professionalsState: StateFlow<UiState<List<ProfessionalEntity>>> = _professionalsState.asStateFlow()

    fun setLoading() {
        _professionalsState.value = UiState.Loading
    }

    fun fetchProfessionalAvailable() {
        viewModelScope.launch {
            fetchProfessionalAvailableUseCase()
                .catch { error -> _professionalsState.value = UiState.Error("${error.localizedMessage}") }
                .collect { state ->
                    _professionalsState.value = state
                }
        }
    }
}
