package com.glowbook.android.domain.usecase

import com.glowbook.android.domain.entity.ProfessionalEntity
import com.glowbook.android.domain.entity.toEntity
import com.glowbook.android.domain.repository.ProfessionalsRepository
import com.glowbook.android.utils.ApiState
import com.glowbook.android.utils.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FetchProfessionalAvailableUseCase @Inject constructor(
    private val professionalsRepository: ProfessionalsRepository
) {
    operator fun invoke(): Flow<UiState<List<ProfessionalEntity>>> = flow {
        emit(UiState.Loading)
        when (val result = professionalsRepository.fetchProfessionalsAvailable()) {
            is ApiState.Success -> emit(UiState.Success(result.data.map { it.toEntity() }))
            is ApiState.Error -> emit(UiState.Error(result.message))
        }
    }
}
