package com.glowbook.android.domain.usecase

import com.glowbook.android.data.remote.model.CreateAppointmentRequest
import com.glowbook.android.domain.repository.CreateAppointmentRepository
import com.glowbook.android.utils.ApiState
import com.glowbook.android.utils.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class CreateAppointmentUseCase @Inject constructor(
    private val createAppointmentRepository: CreateAppointmentRepository
) {
    fun invoke(
        professionalId: Long,
        serviceIds: List<Long>,
        date: LocalDate,
        time: LocalTime
    ): Flow<UiState<Long>> {
        val request = CreateAppointmentRequest(
            data = date.toString(),
            horario = time.format(DateTimeFormatter.ISO_LOCAL_TIME),
            profissionalId = professionalId,
            servicos = serviceIds
        )

        return createAppointmentRepository.createAppointment(request)
            .map { result ->
                when (result) {
                    is ApiState.Success -> UiState.Success(result.data)
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
            .onStart { emit(UiState.Loading) }
    }
}
