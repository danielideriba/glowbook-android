package com.glowbook.android.domain.usecase

import com.glowbook.android.domain.models.ProfessionalEntity
import com.glowbook.android.domain.models.ProfessionalServicesEntity
import com.glowbook.android.domain.models.ServiceEntity
import com.glowbook.android.domain.repository.ProfessionalServicesRepository
import com.glowbook.android.utils.ApiState
import com.glowbook.android.utils.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FetchProfessionalServicesUseCase @Inject constructor(
    private val professionalServicesRepository: ProfessionalServicesRepository
) {
    fun invoke(professionalId: Long): Flow<UiState<ProfessionalServicesEntity>> {
        return professionalServicesRepository.fetchProfessionalServices(professionalId).map { result ->
            when (result) {
                is ApiState.Success -> {
                    val (professional, services) = result.data
                    UiState.Success(
                        ProfessionalServicesEntity(
                            professional = ProfessionalEntity(
                                id = professional.id,
                                name = professional.nome,
                                specialty = professional.especialidade,
                                rating = professional.avaliacao,
                                avatar = professional.avatar,
                                avatarColor = professional.corFundo,
                                available = professional.disponivel,
                                categoryId = professional.categoriaId
                            ),
                            services = services.map { item ->
                                ServiceEntity(
                                    id = item.id,
                                    name = item.nome,
                                    price = item.preco ?: 0.0,
                                    durationMinutes = item.duracaoMinutos ?: 0,
                                    icon = item.icone,
                                    categoryId = item.categoriaId
                                )
                            }
                        )
                    )
                }
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
