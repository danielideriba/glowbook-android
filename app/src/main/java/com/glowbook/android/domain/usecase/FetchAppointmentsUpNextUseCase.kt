package com.glowbook.android.domain.usecase

import com.glowbook.android.domain.models.AppointmentEntity
import com.glowbook.android.domain.repository.AppointmentsUpNextRepository
import com.glowbook.android.utils.ApiState
import com.glowbook.android.utils.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

class FetchAppointmentsUpNextUseCase @Inject constructor(
    private val appointmentsUpNextRepository: AppointmentsUpNextRepository
) {
    fun invoke(): Flow<UiState<List<AppointmentEntity>>> {
        return appointmentsUpNextRepository.fetchAppointmentsUpNext().map { result ->
            when (result) {
                is ApiState.Success -> UiState.Success(
                    result.data.map { (item, services) ->
                        AppointmentEntity(
                            id = item.id,
                            services = services.joinToString(" + ") { it.nome },
                            price = services.sumOf { it.preco ?: 0.0 },
                            durationMinutes = services.sumOf { it.duracaoMinutos ?: 0 },
                            date = LocalDate.parse(item.data),
                            time = LocalTime.parse(item.horario),
                            status = item.status,
                            professionalId = item.profissionalId,
                            professionalName = item.profissional,
                            professionalAvatar = item.avatar,
                            professionalRating = item.avaliacao,
                            category = item.categoria
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
