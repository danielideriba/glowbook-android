package com.glowbook.android.domain.usecase

import com.glowbook.android.domain.models.ProfessionalEntity
import com.glowbook.android.domain.repository.ProfessionalsRepository
import com.glowbook.android.utils.ApiState
import com.glowbook.android.utils.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FetchProfessionalAvailableUseCase @Inject constructor(
    private val professionalsRepository: ProfessionalsRepository
) {
    fun invoke(limit: Int): Flow<UiState<List<ProfessionalEntity>>> {
        return professionalsRepository.fetchProfessionalsAvailable(limit).map { result ->
            when (result) {
                is ApiState.Success -> UiState.Success(
                    result.data.map { item ->
                        ProfessionalEntity(
                            id = item.id,
                            name = item.nome,
                            specialty = item.especialidade,
                            rating = item.avaliacao,
                            avatar = item.avatar,
                            avatarColor = item.corFundo,
                            available = item.disponivel,
                            categoryId = item.categoriaId
                        )
                    }
                )
                is ApiState.Error -> {
                    val errorMessage = when {
                        result.message.contains(
                            "not found",
                            ignoreCase = true
                        ) -> "Resources not found. Please check the spelling and try again."

                        else -> result.message
                    }
                    UiState.Error(errorMessage)
                }
            }
        }
    }
}
