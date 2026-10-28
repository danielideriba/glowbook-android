package com.glowbook.android.domain.usecase

import com.glowbook.android.domain.models.BookedSlotEntity
import com.glowbook.android.domain.models.ProfessionalCalendarEntity
import com.glowbook.android.domain.models.ProfessionalEntity
import com.glowbook.android.domain.models.ServiceEntity
import com.glowbook.android.domain.repository.ProfessionalCalendarRepository
import com.glowbook.android.utils.ApiState
import com.glowbook.android.utils.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

class FetchProfessionalCalendarUseCase @Inject constructor(
    private val professionalCalendarRepository: ProfessionalCalendarRepository
) {
    fun invoke(
        professionalId: Long,
        serviceIds: List<Long>,
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<UiState<ProfessionalCalendarEntity>> {
        return professionalCalendarRepository.fetchProfessionalCalendar(
            professionalId = professionalId,
            serviceIds = serviceIds,
            startDate = startDate.toString(),
            endDate = endDate.toString()
        ).map { result ->
            when (result) {
                is ApiState.Success -> {
                    val (professional, services, appointments) = result.data
                    UiState.Success(
                        ProfessionalCalendarEntity(
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
                            },
                            bookedSlots = appointments.map { item ->
                                BookedSlotEntity(
                                    date = LocalDate.parse(item.data),
                                    time = LocalTime.parse(item.horario)
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
